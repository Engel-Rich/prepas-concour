package com.mutrix.prepa.application.dto.response.subscription;
import com.mutrix.prepa.application.dto.response.BaseResponse;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
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
@Schema(description = "Détails d'une souscription")
public class SubscriptionResponse extends BaseResponse {

    @Schema(description = "ID de la session de concours")
    private UUID concoursSessionId;

    @Schema(description = "ID de l'utilisateur")
    private UUID userId;

    @Schema(description = "Nombre de souscriptions", example = "1")
    private Integer count;

    @Schema(description = "Statut de la souscription", example = "ACTIVE")
    private SubscriptionStatus status;

    public static SubscriptionResponse fromDomain(Subscription s) {
        return SubscriptionResponse.builder()
                .id(s.getId())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .isActive(s.getIsActive())
                .metadata(s.getMetadata())
                .concoursSessionId(s.getConcoursSessionId())
                .userId(s.getUserId())
                .count(s.getCount())
                .status(s.getStatus())
                .build();
    }
}