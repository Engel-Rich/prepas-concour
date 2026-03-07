package com.mutrix.prepa.application.dto.response.subscription;

import com.mutrix.prepa.application.dto.response.BaseResponse;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
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
@Schema(description = "Détails d'un code de souscription")
public class SubscriptionCodeResponse extends BaseResponse {

    @Schema(description = "Code de souscription", example = "ABC-12345")
    private String code;

    @Schema(description = "ID de la souscription associée")
    private UUID subscriptionId;

    @Schema(description = "Statut du code", example = "UNUSED")
    private CodeStatus status;

    public static SubscriptionCodeResponse fromDomain(SubscriptionCode sc) {
        return SubscriptionCodeResponse.builder()
                .id(sc.getId())
                .createdAt(sc.getCreatedAt())
                .updatedAt(sc.getUpdatedAt())
                .isActive(sc.getIsActive())
                .metadata(sc.getMetadata())
                .code(sc.getCode())
                .subscriptionId(sc.getSubscriptionId())
                .status(sc.getStatus())
                .build();
    }
}