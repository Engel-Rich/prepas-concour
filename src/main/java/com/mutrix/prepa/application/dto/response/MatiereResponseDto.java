package com.mutrix.prepa.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class MatiereResponseDto {

    @Schema(name = "id", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(
            name = "name",
            description = "Nom de la matière",
            example = "Mathematics",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Description détaillée de la matière",
            example = "Cours complet d'algèbre, géométrie et analyse."
    )
    private String description;

    @Schema(
            description = "URL du logo associé à la matière",
            example = "https://cdn.myapp.com/logos/mathematics.png"
    )
    private String logoUrl;

    @Schema(
            description = "Indique si la matière est active et visible par les utilisateurs",
            example = "true"
    )
    private Boolean isActive;

    @Schema(
            description = "Date et heure de création de la matière",
            example = "2026-02-16T10:15:30"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Date et heure de dernière modification de la matière",
            example = "2026-02-17T08:45:12"
    )
    private LocalDateTime updatedAt;

    @Schema(
            description = "Données additionnelles dynamiques sous format clé-valeur",
            example = "{\"level\": \"Advanced\", \"coefficient\": 4}"
    )
    private Map<String, Object> metadata;
}
