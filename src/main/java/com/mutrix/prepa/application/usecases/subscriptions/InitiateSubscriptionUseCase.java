package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.commandes.subscription.CreateSubscriptionCommand;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
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
    private final ConcoursSessionServices concoursSessionServices;
    private final PaymentServiceService paymentServiceService;
    private final PaymentProviderService paymentProviderService;
    private final PaymentFactory paymentFactory;

    @Transactional
    public SubscriptionResponse execute(CreateSubscriptionCommand command, UUID userId) {

        // 1. Récupérer la session de concours pour obtenir le montant de base
        ConcoursSessions session = concoursSessionServices
                .getConcoursSessionById(command.getConcoursSessionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session de concours introuvable : " + command.getConcoursSessionId()));

        if (session.getAmount() == null) {
            throw new IllegalStateException("Le montant de la session n'est pas défini");
        }

        // 2. Montant de base = prix unitaire × nombre de places
        int count = command.getCount() != null ? command.getCount() : 1;
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
        paymentStrategy.initiate(
                command.getPaymentServiceId(),
                savedSubscription.getId(),
                userId,
                amountToProcess,
                sens,
                command.getPhoneNumber()
        );

        return SubscriptionResponse.fromDomain(savedSubscription);
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
