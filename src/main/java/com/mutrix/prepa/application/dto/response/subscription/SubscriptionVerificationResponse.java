package com.mutrix.prepa.application.dto.response.subscription;

import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Résultat d'une relance manuelle de vérification de paiement. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Résultat de la vérification manuelle d'une souscription")
public class SubscriptionVerificationResponse {

    @Schema(description = "Issue de la vérification")
    public enum Outcome {
        /** Paiement confirmé : souscription activée ou codes générés. */
        FINALIZED,
        /** La souscription était déjà finalisée — rien à faire. */
        ALREADY_FINALIZED,
        /** Le fournisseur signale un paiement toujours en cours. */
        STILL_PENDING,
        /** Paiement échoué ou annulé : souscription clôturée. */
        PAYMENT_FAILED,
        /** Aucune transaction rattachée à cette souscription. */
        NO_TRANSACTION,
        /** Vérification impossible (référence externe ou fournisseur manquant). */
        NOT_VERIFIABLE,
    }

    @Schema(description = "Issue de l'opération")
    private Outcome outcome;

    @Schema(description = "Message lisible destiné à l'opérateur")
    private String message;

    @Schema(description = "ID de la souscription")
    private UUID subscriptionId;

    @Schema(description = "Statut de la souscription avant la vérification")
    private SubscriptionStatus previousStatus;

    @Schema(description = "Statut de la souscription après la vérification")
    private SubscriptionStatus currentStatus;

    @Schema(description = "ID de la transaction examinée")
    private UUID transactionId;

    @Schema(description = "Référence de la transaction")
    private String transactionReference;

    @Schema(description = "Statut de la transaction après vérification auprès du fournisseur")
    private TransactionStatus transactionStatus;

    @Schema(description = "Nombre de codes d'activation existants après finalisation")
    private Integer codesCount;

    @Schema(description = "Vrai si la vérification a modifié l'état de la souscription")
    private Boolean changed;
}
