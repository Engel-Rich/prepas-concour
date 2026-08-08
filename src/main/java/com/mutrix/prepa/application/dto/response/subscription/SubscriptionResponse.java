package com.mutrix.prepa.application.dto.response.subscription;
import com.mutrix.prepa.application.dto.response.BaseResponse;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
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

    // ── Champs enrichis (session + concours) ──────────────────────────────────

    @Schema(description = "Nom de la session")
    private String sessionName;

    @Schema(description = "Date de début de la session")
    private LocalDate sessionStartDate;

    @Schema(description = "Date de fin de la session")
    private LocalDate sessionEndDate;

    @Schema(description = "Statut de la session", example = "OPEN")
    private String sessionStatus;

    @Schema(description = "Montant de la session")
    private Double sessionAmount;

    @Schema(description = "ID du concours")
    private UUID concoursId;

    @Schema(description = "Nom du concours")
    private String concoursName;

    @Schema(description = "URL du logo du concours")
    private String concoursLogoUrl;

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

    public static SubscriptionResponse fromDomainEnriched(
            Subscription s,
            ConcoursSessions session,
            Concours concours
    ) {
        SubscriptionResponse.SubscriptionResponseBuilder<?, ?> builder = SubscriptionResponse.builder()
                .id(s.getId())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .isActive(s.getIsActive())
                .metadata(s.getMetadata())
                .concoursSessionId(s.getConcoursSessionId())
                .userId(s.getUserId())
                .count(s.getCount())
                .status(s.getStatus());

        if (session != null) {
            builder.sessionName(session.getName())
                   .sessionStartDate(session.getStartDate())
                   .sessionEndDate(session.getEndDate())
                   .sessionStatus(session.getStatus() != null ? session.getStatus().name() : null)
                   .sessionAmount(session.getAmount());
        }

        if (concours != null) {
            builder.concoursId(concours.getId())
                   .concoursName(concours.getName())
                   .concoursLogoUrl(concours.getLogoUrl());
        }

        return builder.build();
    }
}