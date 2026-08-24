package com.mutrix.prepa.application.dto.response.cours;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Clé de contenu délivrée au client autorisé.
 *
 * <p>Transmise uniquement en HTTPS, à un utilisateur authentifié possédant un
 * accès actif au cours. Le client la conserve dans son coffre sécurisé pour
 * permettre la lecture hors connexion.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Clé de déchiffrement d'une vidéo de cours")
public class VideoKeyResponse {

    @Schema(description = "Cours concerné")
    private UUID coursId;

    @Schema(description = "Identifiant de la clé — doit correspondre à celui "
            + "inscrit dans l'en-tête du conteneur")
    private UUID keyId;

    @Schema(description = "Clé de contenu AES-256 encodée en base64")
    private String key;

    @Schema(description = "Schéma du conteneur", example = "AES-256-GCM/CHUNKED")
    private String algorithm;
}
