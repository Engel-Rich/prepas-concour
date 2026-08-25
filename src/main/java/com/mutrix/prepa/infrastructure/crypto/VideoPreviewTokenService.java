package com.mutrix.prepa.infrastructure.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Jeton de prévisualisation à courte durée de vie.
 *
 * <p>Une balise {@code <video>} n'envoie pas d'en-tête {@code Authorization} :
 * la console ne peut donc pas lire un flux protégé par le Bearer habituel.
 * Plutôt que de charger toute la vidéo en mémoire via fetch — impossible sur
 * plusieurs centaines de mégaoctets, et incompatible avec la navigation dans
 * le lecteur — l'administrateur demande un jeton signé qu'il place en query
 * string.
 *
 * <p>Le jeton est lié à un cours précis et expire en quelques minutes : sa
 * fuite n'ouvre l'accès qu'à cette vidéo, et brièvement.
 */
@Slf4j
@Service
public class VideoPreviewTokenService {

    private final byte[] secret;
    private final long ttlSeconds;

    public VideoPreviewTokenService(
            @Value("${video.preview.token-secret:}") String configuredSecret,
            @Value("${video.encryption.master-key:}") String masterKey,
            @Value("${video.preview.ttl-seconds:300}") long ttlSeconds) {

        // À défaut de secret dédié, on dérive du matériel de la clé maîtresse.
        // Ce n'est jamais la clé elle-même : elle ne sert qu'au chiffrement.
        String material = !configuredSecret.isBlank() ? configuredSecret : masterKey;
        if (material.isBlank()) {
            throw new IllegalStateException(
                    "Aucun secret disponible pour signer les jetons de prévisualisation. "
                            + "Renseignez video.preview.token-secret ou video.encryption.master-key.");
        }
        this.secret = ("preview:" + material).getBytes(StandardCharsets.UTF_8);
        this.ttlSeconds = ttlSeconds;
    }

    /** Jeton opaque valable pour ce cours, expirant dans {@link #ttlSeconds}. */
    public String issue(UUID coursId) {
        long expiry = Instant.now().getEpochSecond() + ttlSeconds;
        String payload = coursId + ":" + expiry;
        String signature = sign(payload);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((payload + ":" + signature).getBytes(StandardCharsets.UTF_8));
    }

    /** Vrai si le jeton est authentique, non expiré et émis pour ce cours. */
    public boolean isValid(String token, UUID coursId) {
        if (token == null || token.isBlank()) return false;
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":");
            if (parts.length != 3) return false;

            String payload = parts[0] + ":" + parts[1];

            // Comparaison à temps constant : une comparaison naïve laisserait
            // fuiter la signature attendue octet par octet.
            if (!MessageDigest.isEqual(
                    sign(payload).getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                return false;
            }
            if (!parts[0].equals(coursId.toString())) return false;

            return Long.parseLong(parts[1]) >= Instant.now().getEpochSecond();
        } catch (Exception e) {
            log.debug("Jeton de prévisualisation illisible : {}", e.getMessage());
            return false;
        }
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Signature du jeton impossible", e);
        }
    }
}
