package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.commandes.subscription.CreateSubscriptionCommand;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.infrastructure.payment.PaymentFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitiateSubscriptionUseCase {

    private final SubscriptionServices subscriptionServices;
    private final TransactionServices transactionServices;
    private final ConcoursSessionServices concoursSessionServices;
    private final PaymentServiceService paymentServiceService;
    private final PaymentProviderService paymentProviderService;
    private final PaymentFactory paymentFactory;

    @Transactional
    public TransactionResponse execute(CreateSubscriptionCommand command, UUID userId) {

        // 1. Récupérer la session de concours pour obtenir le montant de base
        ConcoursSessions session = concoursSessionServices
                .getConcoursSessionById(command.getConcoursSessionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session de concours introuvable : " + command.getConcoursSessionId()));

        if (!Boolean.TRUE.equals(session.getIsActive())) {
            throw new IllegalStateException("La session de concours n'est pas active");
        }

        // 2. Montant de base = prix unitaire × nombre de places
        int count = command.getCount() != null ? command.getCount() : 1;

        // Un achat groupé (count > 1) produit des codes partageables, pas un accès pour l'acheteur :
        // il reste autorisé même si celui-ci possède déjà un accès actif sur la session.
        if (count == 1 && subscriptionServices.hasOngoingSubscription(userId, command.getConcoursSessionId())) {
            // Chercher la souscription INITIATE/PENDING existante et sa dernière transaction
            Subscription existingSub = subscriptionServices
                    .findOngoingSubscription(userId, command.getConcoursSessionId())
                    .orElse(null);

            if (existingSub == null) {
                // hasOngoing dit true mais seule une RUNNING existe → on bloque
                throw new IllegalStateException("Une souscription est déjà active pour cette session");
            }

            Transaction lastTx = transactionServices
                    .findLatestBySubscriptionId(existingSub.getId())
                    .orElse(null);

            if (lastTx != null && lastTx.getStatus() == TransactionStatus.FAILED) {
                // Paiement précédent échoué → clôturer cette souscription et laisser l'utilisateur recréer
                subscriptionServices.markPaymentFailed(existingSub.getId());
                log.info("Souscription {} marquée PAYMENT_FAILED (transaction {} FAILED) — nouvelle souscription autorisée",
                        existingSub.getId(), lastTx.getId());
            } else {
                throw new IllegalStateException("Une souscription est déjà en cours ou active pour cette session");
            }
        }

        if (session.getAmount() == null) {
            throw new IllegalStateException("Le montant de la session n'est pas défini");
        }

        double baseAmount = session.getAmount() * count;

        // 3. Créer la souscription en statut INITIATE
        Subscription subscription = Subscription.builder()
                .concoursSessionId(command.getConcoursSessionId())
                .userId(userId)
                .count(count)
                .status(SubscriptionStatus.INITIATE)
                .isActive(true)
                .metadata(command.getMetadata())
                .build();
        Subscription savedSubscription = subscriptionServices.save(subscription);

        // 4. Récupérer le service de paiement et son fournisseur
        PaymentService paymentService = paymentServiceService.getById(command.getPaymentServiceId());
        PaymentProvider provider     = paymentProviderService.getByID(paymentService.getPaymentProviderId());

        // 5. Déterminer le sens réel depuis le service de paiement
        //    (évite de hardcoder TransactionSens.IN pour les futurs services OUT)
        TransactionSens sens = paymentService.getSens() != null
                ? paymentService.getSens()
                : TransactionSens.IN;

        // 6. Calculer le montant ajusté avec les frais
        //    - IN  (dépôt / collecte)  → le client paie le montant + les frais
        //    - OUT (retrait / payout)  → le client reçoit le montant - les frais
        double amountToProcess = applyFees(baseAmount, paymentService, sens);

        log.info("Souscription {} | montant de base : {} | frais total : {:.2f}% | montant {} : {}",
                savedSubscription.getId(),
                baseAmount,
                totalFeeRatePct(paymentService),
                sens,
                amountToProcess);

        // 7. Sélectionner la stratégie de paiement selon le fournisseur
            TransactionPaymentService paymentStrategy = paymentFactory.create(provider.getName());

        // 8. Lancer le paiement — la stratégie crée et persiste la transaction
        Transaction transaction = paymentStrategy.initiate(
                command.getPaymentServiceId(),
                savedSubscription.getId(),
                userId,
                amountToProcess,
                sens,
                command.getPhoneNumber()
        );

        log.info("Statut de la transaction après initiation du paiement : {}", transaction.getStatus());

        // Échec dès l'initiation (fournisseur injoignable, numéro invalide…) :
        // on clôture la souscription qui vient d'être créée.
        // Utiliser `savedSubscription` et non `subscription` : seul le premier
        // porte l'id généré — sans lui, `save()` insérerait une ligne de plus.
        if (transaction.getStatus() == TransactionStatus.FAILED
                || transaction.getStatus() == TransactionStatus.CANCELED) {
            subscriptionServices.markPaymentFailed(savedSubscription.getId());
            log.info("Souscription {} clôturée : paiement refusé à l'initiation",
                    savedSubscription.getId());
        }

        return TransactionResponse.fromDomain(transaction);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retourne le taux combiné (système + fournisseur), 0 si non définis.
     */
    private double combinedFeeRate(PaymentService svc) {
        double systemRate   = svc.getRate()         != null ? svc.getRate()         : 0.0;
        double providerRate = svc.getProviderRate()  != null ? svc.getProviderRate() : 0.0;
        return systemRate + providerRate;
    }

    /**
     * Taux total en % pour les logs.
     */
    private double totalFeeRatePct(PaymentService svc) {
        return combinedFeeRate(svc) * 100.0;
    }

    /**
     * Applique les frais selon le sens :
     * <ul>
     *   <li><b>IN</b>  : amountToCharge = baseAmount × (1 + feeRate)
     *       → le client supporte les frais, nous recevons le montant net.</li>
     *   <li><b>OUT</b> : amountToSend  = baseAmount × (1 − feeRate)
     *       → les frais sont déduits du montant reversé au bénéficiaire.</li>
     * </ul>
     * Le résultat est arrondi à l'entier le plus proche (XAF n'a pas de centimes).
     */
    private double applyFees(double baseAmount, PaymentService svc, TransactionSens sens) {
        double feeRate = combinedFeeRate(svc);
        double adjusted = switch (sens) {
            case IN  -> baseAmount * (1.0 + feeRate);
            case OUT -> baseAmount * (1.0 - feeRate);
        };
        // Arrondir à l'entier (XAF) — changer en Math.round(adjusted * 100) / 100.0 pour d'autres devises
        return Math.round(adjusted);
    }
}
