package com.mutrix.prepa.application.dto.response.subscription;

import com.mutrix.prepa.application.dto.response.BaseResponse;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détails d'un fournisseur de paiement")
public class PaymentProviderResponse extends BaseResponse {

    @Schema(description = "Nom du fournisseur", example = "Orange Money")
    private String name;

    @Schema(description = "Description du fournisseur")
    private String description;

    @Schema(description = "URL du logo", example = "https://cdn.example.com/logo.png")
    private String logoUrl;

    public static PaymentProviderResponse fromDomain(PaymentProvider p) {
        return PaymentProviderResponse.builder()
                .id(p.getId())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .isActive(p.getIsActive())
                .metadata(p.getMetadata())
                .name(p.getName())
                .description(p.getDescription())
                .logoUrl(p.getLogoUrl())
                .build();
    }
}