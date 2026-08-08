package com.mutrix.prepa.infrastructure.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutrix.prepa.application.usecases.subscriptions.codes.ProcessSuccessfulPaymentUseCase;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Traitement asynchrone d'un webhook CamPay.
 * Séparé du contrôleur pour permettre le retour immédiat du 200 au fournisseur,
 * avant toute opération en base de données.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CampayWebhookService {

    private final TransactionServices transactionServices;
    private final SubscriptionServices subscriptionServices;
    private final ProcessSuccessfulPaymentUseCase processSuccessfulPaymentUseCase;
    private final ObjectMapper objectMapper;

    /**
     * Appelé depuis le contrôleur APRÈS l'envoi du 200.
     * S'exécute dans le thread pool de Spring (@Async).
     */
    @Async
    public void process(JsonNode payload) {
        try {
            String status      = payload.path("status").asText(null);
            String externalRef = payload.path("external_reference").asText(null);
            String reason = payload.path("reason").asText(null);

            if (externalRef == null || status == null) {
                log.warn("Webhook CamPay incomplet — external_reference ou status manquant");
                return;
            }

            Optional<Transaction> txOpt = transactionServices.findByReference(externalRef);
            if (txOpt.isEmpty()) {
                log.warn("Webhook CamPay: transaction introuvable pour external_reference={}", externalRef);
                return;
            }

            Transaction tx = txOpt.get();

            TransactionStatus newStatus = switch (status) {
                case "SUCCESSFUL" -> TransactionStatus.SUCCESS;
                case "FAILED"     -> TransactionStatus.FAILED;
                default           -> TransactionStatus.PENDING;
            };

            // Statut inchangé : on ne réécrit pas la transaction, mais on
            // réconcilie quand même la souscription. Une livraison précédente a pu
            // persister le statut final puis échouer avant la finalisation —
            // sortir ici laisserait la souscription bloquée en INITIATE.
            if (newStatus != tx.getStatus()) {
                tx.setStatus(newStatus);
                if (newStatus == TransactionStatus.FAILED) {
                    tx.setRaisonReject(reason != null ? reason : "Paiement refusé — webhook CamPay");
                }
                // Fusion AVANT la persistance, sinon la trace n'est jamais écrite
                mergeProviderResponse(tx, payload);
                transactionServices.update(tx);
            }

            if (tx.getSubscriptionId() != null) {
                if (newStatus == TransactionStatus.SUCCESS) {
                    // Achat individuel → activation ; achat groupé → génération des codes + SMS
                    // (idempotent : une souscription déjà finalisée est ignorée)
                    processSuccessfulPaymentUseCase.execute(tx.getSubscriptionId());
                    log.info("✓ Souscription {} finalisée via webhook CamPay", tx.getSubscriptionId());
                } else if (newStatus == TransactionStatus.FAILED) {
                    subscriptionServices.markPaymentFailed(tx.getSubscriptionId());
                    log.info("✗ Souscription {} clôturée (transaction échouée) via webhook CamPay", tx.getSubscriptionId());
                }
            }

        } catch (Exception e) {
            log.error("Erreur traitement asynchrone webhook CamPay: {}", e.getMessage(), e);
        }


    }

    /**
     * Fusionne le payload brut du webhook dans les métadonnées de la transaction.
     * Le {@link JsonNode} est converti en {@code Map} : c'est ce que la colonne
     * jsonb attend, et cela évite de dépendre de la sérialisation d'un type Jackson.
     */
    private void mergeProviderResponse(Transaction tx, JsonNode payload) {
        try {
            Map<String, Object> metadata = tx.getMetadata() == null
                    ? new HashMap<>()
                    : new HashMap<>(tx.getMetadata());
            metadata.put("providerResponse",
                    objectMapper.convertValue(payload, new TypeReference<Map<String, Object>>() {}));
            metadata.put("ResponseSource","WebHook");
            tx.setMetadata(metadata);
        } catch (Exception e) {
            // Une trace d'audit incomplète ne doit pas bloquer la finalisation
            log.warn("Métadonnées provider non fusionnées pour la transaction {}: {}",
                    tx.getId(), e.getMessage());
        }
    }
}
