package com.mutrix.prepa.application.dto.commandes.concours;

import com.mutrix.prepa.domaines.valueobjects.ConcoursSessionsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "UpdateConcoursSessionDto", description = "DTO utilisé pour modifier une session de concours")
public class UpdateConcoursSessionDto {

    @Schema(
            description = "Nom de la session",
            example = "Session Juin 2026"
    )
    private String name;

    @Schema(
            description = "Description détaillée de la session",
            example = "Session spéciale pour les candidats internes"
    )
    private String description;

    @Schema(
            description = "Date de début de la session",
            example = "2026-06-01"
    )
    private LocalDate startDate;

    @Schema(
            description = "Date de fin de la session",
            example = "2026-06-30"
    )
    private LocalDate endDate;

    @Schema(
            description = "Montant (prix) de la session en FCFA",
            example = "15000"
    )
    private Double amount;

    @Schema(
            description = "Statut actuel de la session",
            example = "OPEN"
    )
    private ConcoursSessionsStatus status;

    @Schema(
            description = "Indique si la session est active",
            example = "true"
    )
    private Boolean isActive;

    @Schema(
            description = "Informations supplémentaires sous format JSON",
            example = "{\"lieu\":\"Yaoundé\", \"capacite\":200}"
    )
    private Map<String, Object> metadata;
}
