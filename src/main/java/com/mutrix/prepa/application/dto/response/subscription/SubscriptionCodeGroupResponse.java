package com.mutrix.prepa.application.dto.response.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Regroupement des codes d'un acheteur par session de concours,
 * utilisé par l'écran « Mes codes » de l'application mobile.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Codes de souscription regroupés par session de concours")
public class SubscriptionCodeGroupResponse {

    @Schema(description = "ID de la session de concours")
    private UUID concoursSessionId;

    @Schema(description = "Nom de la session")
    private String sessionName;

    @Schema(description = "Date de début de la session")
    private LocalDate sessionStartDate;

    @Schema(description = "Date de fin de la session")
    private LocalDate sessionEndDate;

    @Schema(description = "ID du concours")
    private UUID concoursId;

    @Schema(description = "Nom du concours")
    private String concoursName;

    @Schema(description = "URL du logo du concours")
    private String concoursLogoUrl;

    @Schema(description = "Nombre total de codes achetés pour cette session")
    private Integer totalCount;

    @Schema(description = "Nombre de codes encore disponibles")
    private Integer availableCount;

    @Schema(description = "Nombre de codes déjà utilisés")
    private Integer usedCount;

    @Schema(description = "Détail des codes")
    private List<SubscriptionCodeResponse> codes;
}
