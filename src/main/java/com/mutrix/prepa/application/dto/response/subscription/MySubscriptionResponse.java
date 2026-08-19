package com.mutrix.prepa.application.dto.response.subscription;

import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Vue client d'une souscription — ce que l'application mobile est autorisée à voir.
 *
 * <p>Projection volontairement restreinte de {@link SubscriptionResponse} : n'expose
 * que le concours, la session et la souscription elle-même. Sont écartés :
 * <ul>
 *   <li>{@code userId} — identifiant interne, sans usage côté client ;</li>
 *   <li>{@code metadata} — porte des données internes, notamment le code
 *       d'activation ayant ouvert l'accès ({@code code}, {@code codeId}) et
 *       l'identifiant de la souscription d'achat d'un tiers ;</li>
 *   <li>{@code isActive} — drapeau technique de persistance.</li>
 * </ul>
 *
 * <p>Cette classe est la frontière : tout champ ajouté ici devient public.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Souscription telle que vue par son titulaire")
public class MySubscriptionResponse {

    // ── Souscription ──────────────────────────────────────────────────────────

    @Schema(description = "Identifiant de la souscription")
    private UUID id;

    @Schema(description = "Statut de la souscription", example = "RUNNING")
    private SubscriptionStatus status;

    @Schema(description = "Nombre de places achetées", example = "1")
    private Integer count;

    @Schema(description = "Date de souscription")
    private LocalDateTime createdAt;

    @Schema(description = "Dernière mise à jour")
    private LocalDateTime updatedAt;

    // ── Session ───────────────────────────────────────────────────────────────

    @Schema(description = "Identifiant de la session de concours")
    private UUID concoursSessionId;

    @Schema(description = "Nom de la session")
    private String sessionName;

    @Schema(description = "Date de début de la session")
    private LocalDate sessionStartDate;

    @Schema(description = "Date de fin de la session")
    private LocalDate sessionEndDate;

    @Schema(description = "Statut de la session", example = "ONGOING")
    private String sessionStatus;

    @Schema(description = "Prix unitaire de la session")
    private Double sessionAmount;

    // ── Concours ──────────────────────────────────────────────────────────────

    @Schema(description = "Identifiant du concours")
    private UUID concoursId;

    @Schema(description = "Nom du concours")
    private String concoursName;

    @Schema(description = "URL du logo du concours")
    private String concoursLogoUrl;

    /** Projette la réponse interne enrichie vers la vue client. */
    public static MySubscriptionResponse from(SubscriptionResponse s) {
        if (s == null) return null;
        return MySubscriptionResponse.builder()
                .id(s.getId())
                .status(s.getStatus())
                .count(s.getCount())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .concoursSessionId(s.getConcoursSessionId())
                .sessionName(s.getSessionName())
                .sessionStartDate(s.getSessionStartDate())
                .sessionEndDate(s.getSessionEndDate())
                .sessionStatus(s.getSessionStatus())
                .sessionAmount(s.getSessionAmount())
                .concoursId(s.getConcoursId())
                .concoursName(s.getConcoursName())
                .concoursLogoUrl(s.getConcoursLogoUrl())
                .build();
    }
}
