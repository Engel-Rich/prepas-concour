package com.mutrix.prepa.application.dto.commandes.cours;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCoursCommand {

    @NotBlank(message = "Le titre est obligatoire")
    @Schema(
            description = "Titre du cours",
            example = "Introduction à la programmation Java"
    )
    private String title;

    @NotBlank(message = "Le contenu est obligatoire")
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


    @NotNull(message = "La matière est obligatoire")
    @Schema(
            description = "Identifiant de la matière à laquelle appartient le cours",
            example = "123e4567-e89b-12d3-a456-426614174000",
            nullable = true
    )
    private UUID matiereId;

    private Boolean isActive = true;

    private Map<String, Object> metadata;
}