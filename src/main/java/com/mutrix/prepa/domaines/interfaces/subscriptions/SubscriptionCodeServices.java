package com.mutrix.prepa.domaines.interfaces.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionCodeServices {

    SubscriptionCode save(SubscriptionCode code);

    SubscriptionCode getById(UUID id);

    SubscriptionCode getBySubscriptionId(UUID id);

    /**
     * Génère et persiste {@code quantity} codes ACTIVE rattachés à la souscription d'achat.
     * L'unicité du code est garantie par la contrainte d'unicité en base.
     */
    List<SubscriptionCode> generateForSubscription(UUID subscriptionId, int quantity);

    Optional<SubscriptionCode> findByCode(String code);

    /** Tous les codes générés par une souscription d'achat. */
    List<SubscriptionCode> findAllBySubscriptionId(UUID subscriptionId);

    /** Tous les codes achetés par un utilisateur (pour regroupement par concours côté client). */
    List<SubscriptionCode> findAllByBuyer(UUID buyerId);

    Page<SubscriptionCode> searchByBuyer(UUID buyerId, Integer page, Integer size);

    Page<SubscriptionCode> searchByBuyer(UUID buyerId, CodeStatus status, Integer page, Integer size);

    Page<SubscriptionCode> search(Integer page, Integer size);

    Page<SubscriptionCode> search(CodeStatus status, Integer page, Integer size);

    Page<SubscriptionCode> searchBySession(UUID sessionId, Integer page, Integer size);

    Page<SubscriptionCode> searchByConcours(UUID concoursId, Integer page, Integer size);

    /**
     * Consomme un code de façon atomique : marque USED, enregistre l'activateur,
     * la date et la souscription créée à son profit.
     *
     * @throws IllegalStateException si le code a déjà été utilisé ou n'est plus actif.
     */
    SubscriptionCode consume(UUID codeId, UUID usedByUserId, UUID activatedSubscriptionId);

    /**
     * Libère un code consommé à tort : retour à ACTIVE et effacement de la trace
     * d'activation. Le code conserve sa chaîne — l'acheteur a pu la partager.
     */
    SubscriptionCode release(UUID codeId, String reason);

    /** Rend un code définitivement inutilisable (statut → INVALID). */
    SubscriptionCode invalidate(UUID codeId, String reason);

    /** Marque un code comme expiré (session close, statut → EXPIRED). */
    SubscriptionCode markExpired(UUID codeId, String reason);

    long countRemaining(UUID subscriptionId);

    void delete(UUID id);
}
