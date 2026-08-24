package com.mutrix.prepa.domaines.services;

import java.util.Optional;
import java.util.UUID;

/**
 * Cycle de vie des clés de contenu vidéo, selon un schéma d'enveloppe.
 *
 * <p>Chaque vidéo possède sa propre clé (CEK). La CEK n'est jamais stockée en
 * clair : elle est elle-même chiffrée par une clé maîtresse (KEK) détenue par
 * le serveur seul. Compromettre la base ne suffit donc pas à lire les vidéos.
 */
public interface ContentKeyService {

    /** Clé de contenu en clair, prête à l'emploi. */
    record ContentKey(UUID keyId, byte[] key) {}

    /**
     * Crée une clé de contenu pour ce cours et la stocke sous forme chiffrée.
     * Remplace la clé existante le cas échéant (re-chiffrement).
     */
    ContentKey issueFor(UUID coursId);

    /** Récupère et déchiffre la clé d'un cours, si elle existe. */
    Optional<ContentKey> resolveFor(UUID coursId);

    /** Supprime la clé d'un cours. */
    void revokeFor(UUID coursId);
}
