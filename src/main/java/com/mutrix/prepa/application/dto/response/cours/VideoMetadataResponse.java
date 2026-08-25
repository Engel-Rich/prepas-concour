package com.mutrix.prepa.application.dto.response.cours;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Métadonnées du fichier vidéo, nécessaires à une reprise de téléchargement.
 *
 * <p>Le client compare {@code etag} et {@code size} à ceux mémorisés lors du
 * téléchargement partiel : s'ils diffèrent, le fichier distant a changé et le
 * fragment local doit être jeté plutôt que complété — sinon le fichier
 * reconstitué mêlerait deux versions et serait illisible.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Métadonnées de la vidéo d'un cours")
public class VideoMetadataResponse {

    private UUID coursId;

    @Schema(description = "URL de téléchargement")
    private String videoUrl;

    @Schema(description = "Taille totale en octets")
    private Long size;

    @Schema(description = "Empreinte du fichier distant — invalide la reprise si elle change")
    private String etag;

    @Schema(description = "Type MIME du fichier stocké")
    private String contentType;

    @Schema(description = "Vrai si le fichier est un conteneur chiffré")
    private Boolean hasBeenCrypted;

    @Schema(description = "Vrai si le stockage accepte les requêtes Range "
            + "(téléchargement partiel et reprise)")
    private Boolean supportsRange;
}
