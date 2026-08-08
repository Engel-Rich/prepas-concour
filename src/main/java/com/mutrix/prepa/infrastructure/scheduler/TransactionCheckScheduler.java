package com.mutrix.prepa.infrastructure.scheduler;

import com.mutrix.prepa.application.usecases.subscriptions.codes.ProcessSuccessfulPaymentUseCase;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import com.mutrix.prepa.infrastructure.payment.PaymentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vérifie toutes les 5 secondes les transactions PENDING auprès de CamPay
 * et active la souscription associée dès confirmation du paiement.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionCheckScheduler {

    private final TransactionServices transactionServices;
    private final SubscriptionServices subscriptionServices;
    private final ProcessSuccessfulPaymentUseCase processSuccessfulPaymentUseCase;
    private  final PaymentServiceService paymentServiceService;
    private  final PaymentProviderService paymentProviderService;
    private final PaymentFactory paymentFactory;

    @Scheduled(fixedDelay = 5_000)
    public void checkPendingTransactions() {
        List<Transaction> pending = transactionServices.findAllPending();
        if (pending.isEmpty()) return;

        log.debug("Vérification de {} transaction(s) PENDING", pending.size());

        for (Transaction tx : pending) {
            try {
                processOne(tx);
            } catch (Exception e) {
                log.error("Erreur vérification transaction id={}: {}", tx.getId(), e.getMessage());
            }
        }
    }

    private void processOne(Transaction tx) {
        if (tx.getExternalId() == null || tx.getExternalId().isBlank()) {
            // La collecte CamPay a échoué à l'initiation — pas de référence externe
            return;
        }

        PaymentService paymentService = paymentServiceService.getById(tx.getPaymentServiceId());
        PaymentProvider provider     = paymentProviderService.getByID(paymentService.getPaymentProviderId());

        if(provider==null) {
            log.info("Le provider associe a cette transaction est introuvable");
            tx.setStatus(TransactionStatus.FAILED);
            tx.setRaisonReject("Fournisseur de paiement introuvable");
            transactionServices.update(tx);
            finaliseSubscription(tx);
            return;
        }
        TransactionPaymentService paymentStrategy = paymentFactory.create(provider.getName());
        Transaction result = paymentStrategy.verify(tx.getExternalId());

        if (result.getStatus() == TransactionStatus.PENDING) return; // rien de nouveau

        // Mise à jour du statut — la trace du provider est fusionnée AVANT la
        // persistance, sinon elle n'est jamais écrite en base.
        tx.setStatus(result.getStatus());

        if (result.getRaisonReject() != null) tx.setRaisonReject(result.getRaisonReject());
        mergeProviderResponse(tx, result);
        transactionServices.update(tx);

        // La finalisation ne doit jamais dépendre du succès des étapes ci-dessus :
        // une transaction finale sans souscription finalisée est un état bloqué.
        finaliseSubscription(tx);
    }

    /** Fusionne la réponse du provider sans présumer que les métadonnées existent. */
    private void mergeProviderResponse(Transaction tx, Transaction result) {
        try {
            Map<String, Object> metadata = tx.getMetadata() == null
                    ? new HashMap<>()
                    : new HashMap<>(tx.getMetadata());
            metadata.put("providerResponse", result.getMetadata());
            metadata.put("ResponseSource","Scheduler");
            tx.setMetadata(metadata);
        } catch (Exception e) {
            // Une trace d'audit incomplète ne doit pas bloquer la finalisation
            log.warn("Métadonnées provider non fusionnées pour la transaction {}: {}",
                    tx.getId(), e.getMessage());
        }
    }

    private void finaliseSubscription(Transaction tx) {
        if (tx.getSubscriptionId() == null) {
            log.info("Transaction {} → {} (aucune souscription rattachée)", tx.getId(), tx.getStatus());
            return;
        }
        if (tx.getStatus() == TransactionStatus.SUCCESS) {
            // Achat individuel → activation ; achat groupé → génération des codes + SMS
            processSuccessfulPaymentUseCase.execute(tx.getSubscriptionId());
            log.info("✓ Souscription {} finalisée (transaction {} réussie)",
                    tx.getSubscriptionId(), tx.getId());
        } else if (tx.getStatus() == TransactionStatus.FAILED
                || tx.getStatus() == TransactionStatus.CANCELED) {
            subscriptionServices.markPaymentFailed(tx.getSubscriptionId());
            log.info("✗ Souscription {} clôturée (transaction {} échouée)",
                    tx.getSubscriptionId(), tx.getId());
        }
    }
}

