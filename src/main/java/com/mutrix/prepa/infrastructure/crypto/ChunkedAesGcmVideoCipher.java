package com.mutrix.prepa.infrastructure.crypto;

import com.mutrix.prepa.domaines.services.VideoCipherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Conteneur vidéo chiffré en AES-256-GCM par blocs.
 *
 * <p><b>Format « MXV1 »</b> — en-tête de 41 octets, puis les blocs :
 *
 * <pre>
 *  offset  taille  champ
 *  0       4       magic          "MXV1"
 *  4       1       version        0x01
 *  5       4       chunkSize      taille d'un bloc en clair (int, big-endian)
 *  9       8       plaintextSize  taille totale en clair (long)
 *  17      16      keyId          UUID de la clé de contenu
 *  33      8       baseNonce      aléatoire, propre au fichier
 *  ---
 *  puis, pour chaque bloc : [int longueurChiffrée][données chiffrées || tag 16o]
 * </pre>
 *
 * <p><b>Pourquoi par blocs.</b> Un GCM sur le fichier entier obligerait le
 * client à tout déchiffrer avant de pouvoir lire la première seconde, et
 * interdirait le déplacement dans la vidéo. Le découpage permet de ne
 * déchiffrer que les blocs couvrant la plage demandée — ce qui rend aussi
 * possible le support ultérieur de HTTP Range et la reprise de téléchargement.
 *
 * <p><b>Pourquoi aucune donnée additionnelle authentifiée (AAD).</b> Le nonce
 * vaut {@code baseNonce || index} : il est unique par bloc et par fichier.
 * Déplacer un bloc, en rejouer un d'un autre fichier ou en insérer un
 * change le nonce attendu et fait échouer la vérification du tag. La taille
 * en clair inscrite dans l'en-tête détecte par ailleurs toute troncature.
 * Une AAD n'apporterait donc rien, et son absence garde le format
 * implémentable avec les API GCM les plus courantes côté Flutter.
 */
@Slf4j
@Service
public class ChunkedAesGcmVideoCipher implements VideoCipherService {

    public static final byte[] MAGIC = {'M', 'X', 'V', '1'};
    public static final byte VERSION = 1;
    public static final int HEADER_SIZE = 41;

    /** 1 MiB : compromis entre surcoût des tags et granularité de déplacement. */
    public static final int CHUNK_SIZE = 1024 * 1024;

    public static final int GCM_TAG_BITS = 128;
    public static final int GCM_TAG_BYTES = GCM_TAG_BITS / 8;
    public static final int NONCE_SIZE = 12;
    public static final int BASE_NONCE_SIZE = 8;
    public static final int KEY_SIZE = 32;

    private final SecureRandom random = new SecureRandom();

    @Override
    public byte[] generateContentKey() {
        byte[] key = new byte[KEY_SIZE];
        random.nextBytes(key);
        return key;
    }

    @Override
    public long encrypt(InputStream source, OutputStream target,
                        byte[] key, UUID keyId, long plaintextSize) throws Exception {

        if (key == null || key.length != KEY_SIZE) {
            throw new IllegalArgumentException("La clé de contenu doit faire 256 bits");
        }

        byte[] baseNonce = new byte[BASE_NONCE_SIZE];
        random.nextBytes(baseNonce);

        DataOutputStream out = new DataOutputStream(target);
        writeHeader(out, keyId, baseNonce, plaintextSize);

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        byte[] buffer = new byte[CHUNK_SIZE];
        long written = HEADER_SIZE;
        int index = 0;

        while (true) {
            int read = readFully(source, buffer);
            if (read <= 0) break;

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,
                    new GCMParameterSpec(GCM_TAG_BITS, nonceFor(baseNonce, index)));

            byte[] encrypted = cipher.doFinal(buffer, 0, read);
            out.writeInt(encrypted.length);
            out.write(encrypted);

            written += Integer.BYTES + encrypted.length;
            index++;

            if (read < CHUNK_SIZE) break; // fin de flux
        }

        out.flush();
        log.debug("Vidéo chiffrée : {} bloc(s), {} octets en clair -> {} octets",
                index, plaintextSize, written);
        return written;
    }

    @Override
    public long encryptedSizeFor(long plaintextSize) {
        if (plaintextSize <= 0) return HEADER_SIZE;
        long chunks = (plaintextSize + CHUNK_SIZE - 1) / CHUNK_SIZE;
        return HEADER_SIZE + chunks * (Integer.BYTES + GCM_TAG_BYTES) + plaintextSize;
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void writeHeader(DataOutputStream out, UUID keyId, byte[] baseNonce, long plaintextSize)
            throws Exception {
        out.write(MAGIC);
        out.writeByte(VERSION);
        out.writeInt(CHUNK_SIZE);
        out.writeLong(plaintextSize);
        out.writeLong(keyId.getMostSignificantBits());
        out.writeLong(keyId.getLeastSignificantBits());
        out.write(baseNonce);
    }

    /** Nonce de 12 octets : {@code baseNonce (8) || index (4)}. */
    static byte[] nonceFor(byte[] baseNonce, int index) {
        ByteBuffer nonce = ByteBuffer.allocate(NONCE_SIZE);
        nonce.put(baseNonce);
        nonce.putInt(index);
        return nonce.array();
    }

    /**
     * Remplit le tampon autant que possible. {@code InputStream.read} peut
     * rendre la main avant d'avoir rempli le tampon : sans cette boucle, les
     * blocs auraient des tailles arbitraires selon les aléas du réseau ou du
     * disque, et le client ne pourrait plus calculer la position d'un bloc.
     */
    private int readFully(InputStream in, byte[] buffer) throws Exception {
        int total = 0;
        while (total < buffer.length) {
            int read = in.read(buffer, total, buffer.length - total);
            if (read == -1) break;
            total += read;
        }
        return total;
    }
}
