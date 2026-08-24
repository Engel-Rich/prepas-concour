package com.mutrix.prepa.domaines.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Chiffrement des vidéos en conteneur découpé, exploitable en flux.
 *
 * <p>Les implémentations ne doivent jamais charger la vidéo entière en
 * mémoire : elles lisent et écrivent bloc par bloc.
 */
public interface VideoCipherService {

    /**
     * Chiffre {@code source} vers {@code target}.
     *
     * @param key         clé de contenu (32 octets)
     * @param keyId       identifiant de la clé, inscrit dans l'en-tête pour
     *                    permettre la rotation
     * @param plaintextSize taille exacte du contenu en clair
     * @return nombre d'octets écrits dans le conteneur chiffré
     */
    long encrypt(InputStream source, OutputStream target, byte[] key, UUID keyId, long plaintextSize)
            throws Exception;

    /** Taille du conteneur chiffré produit pour un contenu de {@code plaintextSize} octets. */
    long encryptedSizeFor(long plaintextSize);

    /** Génère une clé de contenu aléatoire de 256 bits. */
    byte[] generateContentKey();
}
