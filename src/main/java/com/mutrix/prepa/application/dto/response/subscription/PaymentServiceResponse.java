package com.mutrix.prepa.application.dto.response.subscription;


import com.mutrix.prepa.application.dto.response.BaseResponse;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
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
@Schema(description = "Détails d'un service de paiement")
public class PaymentServiceResponse extends BaseResponse {

    @Schema(description = "URL du logo")
    private String logoUrl;

    @Schema(description = "Nom du service", example = "Orange Money CM")
    private String name;

    @Schema(description = "Description du service")
    private String description;

    @Schema(description = "ID du fournisseur de paiement")
    private UUID paymentProviderId;

    @Schema(description = "Sens de la transaction supporté", example = "DEBIT")
    private TransactionSens sens;

    @Schema(description = "Expression régulière de validation du numéro", example = "^237(69|65)[0-9]{7}$")
    private String regExp;

    @Schema(description = "Taux appliqué par le provider", example = "0.02")
    private Double rate;

    @Schema(description = "Taux appliqué par le systeme", example = "0.02")
    private Double providerRate;

    public static PaymentServiceResponse fromDomain(PaymentService ps) {
        return PaymentServiceResponse.builder()
                .id(ps.getId())
                .createdAt(ps.getCreatedAt())
                .updatedAt(ps.getUpdatedAt())
                .isActive(ps.getIsActive())
                .metadata(ps.getMetadata())
                .logoUrl(ps.getLogoUrl())
                .name(ps.getName())
                .description(ps.getDescription())
                .paymentProviderId(ps.getPaymentProviderId())
                .sens(ps.getSens())
                .regExp(ps.getRegExp())

                .rate(ps.getRate())
                .build();
    }
}