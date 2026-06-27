package com.mutrix.prepa.application.dto.commandes.cours;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Commande permettant de mettre à jour un cours existant")
public class UpdateCoursCommand {
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
            description = "Indique si le cours est actif ou non",
            example = "true",
            nullable = true
    )
    private Boolean gratuit;

    private Boolean isActive;

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
}