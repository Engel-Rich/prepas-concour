package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionVerificationResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionVerificationResponse.Outcome;
import com.mutrix.prepa.application.usecases.subscriptions.codes.ProcessSuccessfulPaymentUseCase;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import com.mutrix.prepa.infrastructure.payment.PaymentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Relance manuelle de la vérification d'un paiement.
 *
 * <p>Filet de rattrapage lorsqu'une souscription reste bloquée en INITIATE/PENDING
 * alors que la transaction porte déjà un statut final : webhook jamais livré,
 * scheduler interrompu, ou erreur survenue entre la persistance de la transaction
 * et la finalisation de la souscription.
 *
 * <p>Déroulé : souscription → dernière transaction → vérification auprès du
 * fournisseur → finalisation (activation ou génération des codes) → notifications.
 * L'opération est idempotente et peut être relancée sans risque.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifySubscriptionPaymentUseCase {

    private final SubscriptionServices subscriptionServices;
    private final TransactionServices transactionServices;
    private final SubscriptionCodeServices subscriptionCodeServices;
    private final PaymentServiceService paymentServiceService;
    private final PaymentProviderService paymentProviderService;
    private final PaymentFactory paymentFactory;
    private final ProcessSuccessfulPaymentUseCase processSuccessfulPaymentUseCase;

    @Transactional
    public SubscriptionVerificationResponse execute(UUID subscriptionId) {
        Subscription subscription = subscriptionServices.getById(subscriptionId.toString());
        SubscriptionStatus previous = subscription.getStatus();

        // Le contrôle se déroule quel que soit l'état : il doit rester lançable
        // depuis la console sur n'importe quelle ligne. Une souscription déjà
        // honorée ne peut pas être dégradée — `activate`/`markCodesIssued` sont
        // idempotents et `markPaymentFailed` refuse de rétrograder RUNNING/CODES_ISSUED.
        boolean alreadyFinalized = previous == SubscriptionStatus.RUNNING
                || previous == SubscriptionStatus.CODES_ISSUED;

        // 1. Dernière transaction rattachée
        Transaction tx = transactionServices.findLatestBySubscriptionId(subscriptionId).orElse(null);
        if (tx == null) {
            return response(Outcome.NO_TRANSACTION,
                    "Aucune transaction n'est rattachée à cette souscription.",
                    subscription, previous, null, false);
        }

        // 2. Vérification auprès du fournisseur — inutile si le statut est déjà
        //    final, on passe alors directement à la réconciliation.
        if (tx.getStatus() != TransactionStatus.SUCCESS
                && tx.getStatus() != TransactionStatus.FAILED
                && tx.getStatus() != TransactionStatus.CANCELED) {

            if (tx.getExternalId() == null || tx.getExternalId().isBlank()) {
                return response(Outcome.NOT_VERIFIABLE,
                        "La transaction n'a pas de référence externe : le paiement n'a jamais été "
                                + "accepté par le fournisseur.",
                        subscription, previous, tx, false);
            }

            PaymentProvider provider = resolveProvider(tx);
            if (provider == null) {
                return response(Outcome.NOT_VERIFIABLE,
                        "Le fournisseur de paiement de cette transaction est introuvable.",
                        subscription, previous, tx, false);
            }

            TransactionPaymentService strategy = paymentFactory.create(provider.getName());
            Transaction verified = strategy.verify(tx.getExternalId());

            if (verified.getStatus() == TransactionStatus.PENDING) {
                return response(
                        alreadyFinalized ? Outcome.ALREADY_FINALIZED : Outcome.STILL_PENDING,
                        alreadyFinalized
                                ? "Souscription déjà finalisée ; le fournisseur signale toutefois "
                                    + "un paiement encore en cours."
                                : "Le fournisseur signale un paiement toujours en cours.",
                        subscription, previous, tx, false);
            }

            tx.setStatus(verified.getStatus());
            if (verified.getRaisonReject() != null) tx.setRaisonReject(verified.getRaisonReject());
            mergeProviderResponse(tx, verified);
            tx = transactionServices.update(tx);
        }

        // 3. Réconciliation de la souscription
        if (tx.getStatus() == TransactionStatus.SUCCESS) {
            // Achat individuel → activation ; achat groupé → codes. Notifications incluses.
            // Sans effet si la souscription est déjà finalisée.
            processSuccessfulPaymentUseCase.execute(subscriptionId);
        } else {
            // Ne rétrograde jamais une souscription déjà honorée.
            subscriptionServices.markPaymentFailed(subscriptionId);
        }

        Subscription refreshed = subscriptionServices.getById(subscriptionId.toString());
        boolean changed = refreshed.getStatus() != previous;

        Outcome outcome;
        String message;
        if (tx.getStatus() == TransactionStatus.SUCCESS) {
            outcome = changed ? Outcome.FINALIZED : Outcome.ALREADY_FINALIZED;
            message = changed
                    ? "Paiement confirmé, souscription finalisée."
                    : "Paiement confirmé — la souscription était déjà finalisée ("
                        + refreshed.getStatus() + ").";
        } else if (alreadyFinalized) {
            // Cas limite : paiement en échec sur une souscription déjà honorée.
            // On préserve l'accès et on le signale à l'opérateur.
            outcome = Outcome.ALREADY_FINALIZED;
            message = "Le fournisseur signale un paiement " + tx.getStatus()
                    + ", mais la souscription est déjà honorée (" + refreshed.getStatus()
                    + ") : l'accès a été conservé. À examiner.";
        } else {
            outcome = Outcome.PAYMENT_FAILED;
            message = "Le paiement a échoué : la souscription a été clôturée.";
        }

        log.info("Vérification manuelle : souscription {} — {} ({} → {}), transaction {}",
                subscriptionId, outcome, previous, refreshed.getStatus(), tx.getStatus());

        return response(outcome, message, refreshed, previous, tx, changed);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private PaymentProvider resolveProvider(Transaction tx) {
        try {
            PaymentService paymentService = paymentServiceService.getById(tx.getPaymentServiceId());
            if (paymentService == null) return null;
            return paymentProviderService.getByID(paymentService.getPaymentProviderId());
        } catch (Exception e) {
            log.warn("Fournisseur non résolu pour la transaction {}: {}", tx.getId(), e.getMessage());
            return null;
        }
    }

    /** Fusionne la trace du fournisseur sans présumer que les métadonnées existent. */
    private void mergeProviderResponse(Transaction tx, Transaction verified) {
        try {
            Map<String, Object> metadata = tx.getMetadata() == null
                    ? new HashMap<>()
                    : new HashMap<>(tx.getMetadata());
            metadata.put("providerResponse", verified.getMetadata());
            metadata.put("manualVerification", true);
            tx.setMetadata(metadata);
        } catch (Exception e) {
            log.warn("Métadonnées provider non fusionnées pour la transaction {}: {}",
                    tx.getId(), e.getMessage());
        }
    }

    private SubscriptionVerificationResponse response(
            Outcome outcome,
            String message,
            Subscription subscription,
            SubscriptionStatus previous,
            Transaction tx,
            boolean changed) {

        Integer codesCount = null;
        try {
            if (subscription.getStatus() == SubscriptionStatus.CODES_ISSUED) {
                codesCount = subscriptionCodeServices.findAllBySubscriptionId(subscription.getId()).size();
            }
        } catch (Exception ignored) {
            // comptage indicatif — son échec ne doit pas masquer le résultat
        }

        return SubscriptionVerificationResponse.builder()
                .outcome(outcome)
                .message(message)
                .subscriptionId(subscription.getId())
                .previousStatus(previous)
                .currentStatus(subscription.getStatus())
                .transactionId(tx != null ? tx.getId() : null)
                .transactionReference(tx != null ? tx.getReference() : null)
                .transactionStatus(tx != null ? tx.getStatus() : null)
                .codesCount(codesCount)
                .changed(changed)
                .build();
    }
}
