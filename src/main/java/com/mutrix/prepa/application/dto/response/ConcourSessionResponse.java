package com.mutrix.prepa.application.dto.response;

import com.mutrix.prepa.domaines.valueobjects.ConcoursSessionsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "ConcourSessionResponse",
        description = "Représentation détaillée d’une session de concours incluant les informations du concours associé"
)
public class ConcourSessionResponse {

    @Schema(
            description = "Identifiant unique de la session de concours",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID id;

    @Schema(
            description = "Informations complètes du concours auquel appartient cette session"
    )
    private ConcourResponseDTO concours;

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
            description = "Date de début officielle de la session",
            example = "2026-06-01"
    )
    private LocalDate startDate;

    @Schema(
            description = "Date de fin officielle de la session",
            example = "2026-06-30"
    )
    private LocalDate endDate;

    @Schema(description = "Montant de la session (frais d'inscription)", example = "5000.0")
    private Double amount;

    @Schema(
            description = "Statut actuel de la session",
            example = "OPEN",
            allowableValues = {"DRAFT", "OPEN", "CLOSED", "CANCELLED"}
    )
    private ConcoursSessionsStatus status;

    @Schema(
            description = "Indique si la session est active dans le système",
            example = "true"
    )
    private Boolean isActive;

    @Schema(
            description = "Date de création de la session",
            example = "2026-01-10T10:15:30"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Date de dernière modification de la session",
            example = "2026-01-15T14:20:00"
    )
    private LocalDateTime updatedAt;

    @Schema(
            description = "Informations supplémentaires stockées sous format JSON",
            example = "{\"lieu\":\"Yaoundé\", \"capacite\":200}"
    )
    private Map<String, Object> metadata;
}
