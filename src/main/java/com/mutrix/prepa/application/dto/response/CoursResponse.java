package com.mutrix.prepa.application.dto.response;

import com.mutrix.prepa.domaines.models.Cours;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursResponse {

    @Schema(
            description = "Identifiant unique du cours",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;
    @Schema(
            description = "Titre du cours",
            example = "Introduction à la programmation Java"
    )
    private String title;
    @Schema(
            description = "Contenu détaillé du cours (texte, markdown, HTML, etc.)",
            example = "Dans ce cours nous allons découvrir les bases de Java..."
    )
    private String body;
    @Schema(
            description = "URL de la vidéo associée au cours (optionnel)",
            example = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
            nullable = true
    )
    private String videoUrl;
    @Schema(
            description = "Identifiant de la matière à laquelle appartient le cours",
            example = "123e4567-e89b-12d3-a456-426614174000",
            nullable = true
    )
    private UUID matiereId;
    @Schema(
            description = "Identifiant de l'utilisateur (auteur/propriétaire du cours)",
            example = "223e4567-e89b-12d3-a456-426614174111"
    )
    private UUID userId;
    @Schema(
            description = "Indique si le cours est actif ou non",
            example = "true"
    )
    private Boolean isActive;
    @Schema(
            description = "Date et heure de création du cours (au format ISO-8601)",
            example = "2025-01-15T10:30:00"
    )
    private LocalDateTime createdAt;
    @Schema(
            description = "Date et heure de la dernière mise à jour du cours (au format ISO-8601)",
            example = "2025-01-20T14:45:00",
            nullable = true
    )
    private LocalDateTime updatedAt;
    @Schema(
            description = "Métadonnées supplémentaires associées au cours (tags, niveau, durée, etc.)",
            example = """
        {
          "niveau": "Débutant",
          "duree": 90,
          "tags": ["java", "programmation", "backend"]
        }
        """,
            nullable = true
    )
    private Map<String, Object> metadata;

    public static CoursResponse fromDomain(Cours cours) {
        return CoursResponse.builder()
                .id(cours.getId())
                .title(cours.getTitle())
                .body(cours.getBody())
                .videoUrl(cours.getVideoUrl())
                .matiereId(cours.getMatiereId())
                .userId(cours.getUserId())
                .isActive(cours.getIsActive())
                .createdAt(cours.getCreatedAt())
                .updatedAt(cours.getUpdatedAt())
                .metadata(cours.getMetadata())
                .build();
    }
}
