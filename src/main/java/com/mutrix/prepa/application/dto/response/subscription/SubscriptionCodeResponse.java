package com.mutrix.prepa.application.dto.response.subscription;

import com.mutrix.prepa.application.dto.response.BaseResponse;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Détails d'un code de souscription")
public class SubscriptionCodeResponse extends BaseResponse {

    @Schema(description = "Code d'activation", example = "AB7K-9XMP")
    private String code;

    @Schema(description = "ID de la souscription d'achat qui a généré ce code")
    private UUID subscriptionId;

    @Schema(description = "Statut du code", example = "ACTIVE")
    private CodeStatus status;

    // ── Consommation ──────────────────────────────────────────────────────────

    @Schema(description = "ID de l'utilisateur ayant activé le code")
    private UUID usedByUserId;

    @Schema(description = "Nom de l'utilisateur ayant activé le code")
    private String usedByName;

    @Schema(description = "Téléphone de l'utilisateur ayant activé le code")
    private String usedByPhone;

    @Schema(description = "Date d'activation du code")
    private LocalDateTime usedAt;

    @Schema(description = "Souscription créée au profit de l'activateur")
    private UUID activatedSubscriptionId;

    // ── Acheteur ──────────────────────────────────────────────────────────────

    @Schema(description = "ID de l'acheteur du lot de codes")
    private UUID buyerId;

    @Schema(description = "Nom de l'acheteur")
    private String buyerName;

    @Schema(description = "Téléphone de l'acheteur")
    private String buyerPhone;

    // ── Session + concours ────────────────────────────────────────────────────

    @Schema(description = "ID de la session de concours")
    private UUID concoursSessionId;

    @Schema(description = "Nom de la session")
    private String sessionName;

    @Schema(description = "Date de début de la session")
    private LocalDate sessionStartDate;

    @Schema(description = "Date de fin de la session")
    private LocalDate sessionEndDate;

    @Schema(description = "Montant unitaire de la session")
    private Double sessionAmount;

    @Schema(description = "ID du concours")
    private UUID concoursId;

    @Schema(description = "Nom du concours")
    private String concoursName;

    @Schema(description = "URL du logo du concours")
    private String concoursLogoUrl;

    public static SubscriptionCodeResponse fromDomain(SubscriptionCode sc) {
        return baseBuilder(sc).build();
    }

    public static SubscriptionCodeResponse fromDomainEnriched(
            SubscriptionCode sc,
            ConcoursSessions session,
            Concours concours,
            UserModel buyer,
            UserModel activator
    ) {
        SubscriptionCodeResponse.SubscriptionCodeResponseBuilder<?, ?> builder = baseBuilder(sc);

        if (session != null) {
            builder.concoursSessionId(session.getId())
                   .sessionName(session.getName())
                   .sessionStartDate(session.getStartDate())
                   .sessionEndDate(session.getEndDate())
                   .sessionAmount(session.getAmount());
        }
        if (concours != null) {
            builder.concoursId(concours.getId())
                   .concoursName(concours.getName())
                   .concoursLogoUrl(concours.getLogoUrl());
        }
        if (buyer != null) {
            builder.buyerId(buyer.getId())
                   .buyerName(buyer.getName())
                   .buyerPhone(buyer.getPhone());
        }
        if (activator != null) {
            builder.usedByName(activator.getName())
                   .usedByPhone(activator.getPhone());
        }
        return builder.build();
    }

    private static SubscriptionCodeResponse.SubscriptionCodeResponseBuilder<?, ?> baseBuilder(SubscriptionCode sc) {
        return SubscriptionCodeResponse.builder()
                .id(sc.getId())
                .createdAt(sc.getCreatedAt())
                .updatedAt(sc.getUpdatedAt())
                .isActive(sc.getIsActive())
                .metadata(sc.getMetadata())
                .code(sc.getCode())
                .subscriptionId(sc.getSubscriptionId())
                .status(sc.getStatus())
                .usedByUserId(sc.getUsedByUserId())
                .usedAt(sc.getUsedAt())
                .activatedSubscriptionId(sc.getActivatedSubscriptionId());
    }
}
