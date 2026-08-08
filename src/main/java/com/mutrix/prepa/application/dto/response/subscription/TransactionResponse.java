package com.mutrix.prepa.application.dto.response.subscription;

import com.mutrix.prepa.application.dto.response.BaseResponse;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détails d'une transaction")
public class TransactionResponse extends BaseResponse {

    @Schema(description = "Référence unique de la transaction", example = "TXN-20240101-001")
    private String reference;

    @Schema(description = "Montant de la transaction", example = "5000.0")
    private Double amount;

    @Schema(description = "ID de la souscription associée")
    private UUID subscriptionId;

    @Schema(description = "ID de l'utilisateur")
    private UUID userId;

    @Schema(description = "ID du service de paiement utilisé")
    private UUID paymentServiceId;

    @Schema(description = "Statut de la transaction", example = "PENDING")
    private TransactionStatus status;

    @Schema(description = "Sens de la transaction", example = "DEBIT")
    private TransactionSens sens;

    @Schema(description = "Raison du rejet si applicable")
    private String raisonReject;

    @Schema(description = "Token de paiement")
    private String payToken;

    @Schema(description = "Identifiant externe chez le fournisseur")
    private String externalId;

    @Schema(description = "Numéro de téléphone utilisé", example = "237690000000")
    private String phoneNumber;

    @Schema(description = "Meta donne ")

    public static TransactionResponse fromDomain(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .isActive(t.getIsActive())
                .metadata(t.getMetadata())
                .reference(t.getReference())
                .amount(t.getAmount())
                .subscriptionId(t.getSubscriptionId())
                .userId(t.getUserId())
                .paymentServiceId(t.getPaymentServiceId())
                .status(t.getStatus())
                .sens(t.getSens())
                .raisonReject(t.getRaisonReject())
                .payToken(t.getPayToken())
                .externalId(t.getExternalId())
                .phoneNumber(t.getPhoneNumber())
                .build();
    }
}