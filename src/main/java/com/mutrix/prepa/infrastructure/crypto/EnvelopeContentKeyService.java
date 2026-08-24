package com.mutrix.prepa.infrastructure.crypto;

import com.mutrix.prepa.domaines.services.ContentKeyService;
import com.mutrix.prepa.domaines.services.VideoCipherService;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursVideoKeyRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursVideoKeyEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Chiffrement d'enveloppe des clés de contenu.
 *
 * <p>La clé maîtresse (KEK) vient de la configuration et ne quitte jamais le
 * serveur. Chaque clé de contenu (CEK) est chiffrée par la KEK avant d'être
 * stockée : la base seule ne permet donc pas de déchiffrer une vidéo.
 *
 * <p>La KEK n'est <b>jamais</b> journalisée, et n'est exposée par aucune route.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnvelopeContentKeyService implements ContentKeyService {

    private static final String ALGORITHM = "AES-256-GCM/CHUNKED";
    private static final int WRAP_NONCE_SIZE = 12;
    private static final int GCM_TAG_BITS = 128;

    private final CoursVideoKeyRepository repository;
    private final VideoCipherService videoCipherService;
    private final SecureRandom random = new SecureRandom();

    /** Clé maîtresse en base64, 32 octets une fois décodée. */
    @Value("${video.encryption.master-key:}")
    private String masterKeyBase64;

    private SecretKeySpec masterKey;

    @PostConstruct
    void loadMasterKey() {
        if (masterKeyBase64 == null || masterKeyBase64.isBlank()) {
            log.warn("[Crypto] video.encryption.master-key absente — le chiffrement des "
                    + "vidéos est désactivé, les dépôts se feront en clair.");
            return;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(masterKeyBase64.trim());
            if (decoded.length != 32) {
                log.error("[Crypto] Clé maîtresse invalide : {} octets attendus 32. "
                        + "Chiffrement désactivé.", decoded.length);
                return;
            }
            this.masterKey = new SecretKeySpec(decoded, "AES");
            log.info("[Crypto] Clé maîtresse chargée, chiffrement des vidéos actif.");
        } catch (IllegalArgumentException e) {
            // Message volontairement sans détail : ne rien révéler du secret.
            log.error("[Crypto] Clé maîtresse illisible (base64 invalide). Chiffrement désactivé.");
        }
    }

    /** Le chiffrement n'est possible que si la clé maîtresse est disponible. */
    public boolean isAvailable() {
        return masterKey != null;
    }

    @Override
    @Transactional
    public ContentKey issueFor(UUID coursId) {
        requireMasterKey();

        byte[] cek = videoCipherService.generateContentKey();
        UUID keyId = UUID.randomUUID();

        CoursVideoKeyEntity entity = repository.findByCoursId(coursId)
                .orElseGet(() -> CoursVideoKeyEntity.builder().coursId(coursId).build());

        entity.setKeyId(keyId);
        entity.setWrappedKey(wrap(cek));
        entity.setAlgorithm(ALGORITHM);
        repository.save(entity);

        return new ContentKey(keyId, cek);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContentKey> resolveFor(UUID coursId) {
        if (!isAvailable()) return Optional.empty();
        return repository.findByCoursId(coursId)
                .map(entity -> new ContentKey(entity.getKeyId(), unwrap(entity.getWrappedKey())));
    }

    @Override
    @Transactional
    public void revokeFor(UUID coursId) {
        repository.deleteByCoursId(coursId);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void requireMasterKey() {
        if (!isAvailable()) {
            throw new IllegalStateException(
                    "Clé maîtresse de chiffrement absente : configurez video.encryption.master-key");
        }
    }

    /** Produit {@code base64(nonce || CEK chiffrée || tag)}. */
    private String wrap(byte[] cek) {
        try {
            byte[] nonce = new byte[WRAP_NONCE_SIZE];
            random.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, masterKey, new GCMParameterSpec(GCM_TAG_BITS, nonce));
            byte[] sealed = cipher.doFinal(cek);

            ByteBuffer buffer = ByteBuffer.allocate(nonce.length + sealed.length);
            buffer.put(nonce).put(sealed);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            // Ne jamais propager le détail : il pourrait porter des éléments de clé.
            log.error("[Crypto] Échec de l'encapsulation de la clé de contenu");
            throw new IllegalStateException("Impossible de sécuriser la clé de contenu");
        }
    }

    private byte[] unwrap(String wrapped) {
        try {
            byte[] raw = Base64.getDecoder().decode(wrapped);
            byte[] nonce = new byte[WRAP_NONCE_SIZE];
            byte[] sealed = new byte[raw.length - WRAP_NONCE_SIZE];
            System.arraycopy(raw, 0, nonce, 0, WRAP_NONCE_SIZE);
            System.arraycopy(raw, WRAP_NONCE_SIZE, sealed, 0, sealed.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, masterKey, new GCMParameterSpec(GCM_TAG_BITS, nonce));
            return cipher.doFinal(sealed);
        } catch (Exception e) {
            log.error("[Crypto] Échec du déballage d'une clé de contenu — clé maîtresse "
                    + "changée ou donnée corrompue");
            throw new IllegalStateException("Clé de contenu illisible");
        }
    }
}
