package com.mutrix.prepa.application.dto.response.cours;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Résultat d'une tentative de chiffrement de vidéo. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Résultat du chiffrement d'une vidéo de cours")
public class VideoEncryptionResponse {

    @Schema(description = "Issue de l'opération")
    public enum Outcome {
        /** Vidéo chiffrée et remplacée dans le bucket. */
        ENCRYPTED,
        /** La vidéo était déjà chiffrée — aucune action. */
        ALREADY_ENCRYPTED,
        /** Le cours ne porte aucune vidéo. */
        NO_VIDEO,
        /** Chiffrement indisponible ou en échec ; la vidéo reste en clair. */
        FAILED,
    }

    private Outcome outcome;

    @Schema(description = "Message destiné à l'opérateur")
    private String message;

    @Schema(description = "Étape atteinte avant l'échec, pour le diagnostic",
            example = "UPLOAD")
    private String stage;

    @Schema(description = "Détail technique de l'erreur — jamais un secret serveur")
    private String errorDetail;

    private UUID coursId;

    @Schema(description = "État du cours après l'opération")
    private Boolean hasBeenCrypted;

    @Schema(description = "Nouvelle URL de la vidéo si elle a été remplacée")
    private String videoUrl;
}
