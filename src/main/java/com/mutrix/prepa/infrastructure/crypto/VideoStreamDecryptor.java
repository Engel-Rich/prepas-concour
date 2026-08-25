package com.mutrix.prepa.infrastructure.crypto;

import com.mutrix.prepa.infrastructure.services.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import static com.mutrix.prepa.infrastructure.crypto.ChunkedAesGcmVideoCipher.*;

/**
 * Déchiffrement en flux d'un conteneur MXV1 stocké sur MinIO.
 *
 * <p>Sert la prévisualisation depuis la console d'administration : le serveur
 * déchiffre à la volée et renvoie de la vidéo en clair, si bien que la clé de
 * contenu <b>ne quitte jamais le serveur</b>. C'est une différence assumée avec
 * le mobile, qui reçoit la clé pour pouvoir lire hors connexion : un navigateur
 * n'a pas de coffre équivalent au Keychain, et une clé passée au JavaScript
 * serait lisible dans les outils de développement.
 *
 * <p>Seuls les blocs couvrant la plage demandée sont rapatriés depuis MinIO :
 * une vidéo de 2 Go peut être parcourue sans jamais être chargée entièrement.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoStreamDecryptor {

    private final MinioService minioService;

    /** En-tête du conteneur : tout ce qu'il faut pour localiser un bloc. */
    public record Header(UUID keyId, int chunkSize, long plaintextSize, byte[] baseNonce) {

        /** Taille d'un bloc chiffré : le clair plus le préfixe de longueur et le tag. */
        public int encryptedChunkSize() {
            return Integer.BYTES + chunkSize + GCM_TAG_BYTES;
        }

        public int chunkCount() {
            if (plaintextSize <= 0) return 0;
            return (int) ((plaintextSize + chunkSize - 1) / chunkSize);
        }
    }

    /** Lit l'en-tête sans rapatrier le corps du fichier. */
    public Header readHeader(String objectName) {
        try (InputStream in = minioService.openRange(objectName, 0, HEADER_SIZE);
             DataInputStream data = new DataInputStream(in)) {

            byte[] magic = new byte[MAGIC.length];
            data.readFully(magic);
            for (int i = 0; i < MAGIC.length; i++) {
                if (magic[i] != MAGIC[i]) {
                    throw new IllegalStateException("Ce fichier n'est pas un conteneur MXV1");
                }
            }

            byte version = data.readByte();
            if (version != VERSION) {
                throw new IllegalStateException("Version de conteneur non supportée : " + version);
            }

            int chunkSize = data.readInt();
            long plaintextSize = data.readLong();
            UUID keyId = new UUID(data.readLong(), data.readLong());
            byte[] baseNonce = new byte[BASE_NONCE_SIZE];
            data.readFully(baseNonce);

            return new Header(keyId, chunkSize, plaintextSize, baseNonce);

        } catch (Exception e) {
            throw new RuntimeException("Lecture de l'en-tête impossible : " + e.getMessage(), e);
        }
    }

    /**
     * Écrit dans {@code out} le clair correspondant à {@code [from, to]}.
     *
     * <p>Les bornes portent sur le contenu <b>en clair</b> : c'est ce que le
     * lecteur du navigateur demande via ses en-têtes Range. La conversion vers
     * les offsets chiffrés tient compte du préfixe de longueur et du tag de
     * chaque bloc.
     */
    public void streamRange(String objectName, byte[] key, Header header,
                            long from, long to, OutputStream out) throws Exception {

        if (from < 0 || to >= header.plaintextSize() || from > to) {
            throw new IllegalArgumentException("Plage demandée hors limites");
        }

        int firstChunk = (int) (from / header.chunkSize());
        int lastChunk = (int) (to / header.chunkSize());

        long encryptedOffset = (long) HEADER_SIZE + (long) firstChunk * header.encryptedChunkSize();
        long encryptedLength = (long) (lastChunk - firstChunk + 1) * header.encryptedChunkSize();

        // Le dernier bloc est plus court : ne pas déborder de l'objet.
        long objectSize = minioService.objectSize(objectName);
        encryptedLength = Math.min(encryptedLength, objectSize - encryptedOffset);

        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        long produced = 0;
        long toProduce = to - from + 1;

        try (InputStream in = minioService.openRange(objectName, encryptedOffset, encryptedLength);
             DataInputStream data = new DataInputStream(in)) {

            for (int index = firstChunk; index <= lastChunk && produced < toProduce; index++) {
                int cipherLen = data.readInt();
                byte[] cipherText = new byte[cipherLen];
                data.readFully(cipherText);

                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec,
                        new GCMParameterSpec(GCM_TAG_BITS, nonceFor(header.baseNonce(), index)));
                byte[] plain = cipher.doFinal(cipherText);

                // Le premier bloc démarre avant la borne demandée, le dernier
                // la dépasse : on ne renvoie que la portion utile.
                long chunkStart = (long) index * header.chunkSize();
                int offsetInChunk = (int) Math.max(0, from - chunkStart);
                int available = plain.length - offsetInChunk;
                int length = (int) Math.min(available, toProduce - produced);

                if (length > 0) {
                    out.write(plain, offsetInChunk, length);
                    produced += length;
                }
            }
            out.flush();
        }
    }

    /**
     * Nonce d'un bloc : préfixe aléatoire du fichier suivi de l'index.
     *
     * <p>Cette construction rend inutile toute donnée authentifiée
     * supplémentaire : un bloc déplacé ou provenant d'un autre fichier serait
     * déchiffré avec un nonce différent, et son tag GCM échouerait.
     */
    private byte[] nonceFor(byte[] baseNonce, int chunkIndex) {
        return ByteBuffer.allocate(NONCE_SIZE)
                .put(baseNonce)
                .putInt(chunkIndex)
                .array();
    }
}
