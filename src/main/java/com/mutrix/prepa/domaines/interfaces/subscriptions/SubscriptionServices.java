package com.mutrix.prepa.domaines.interfaces.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionServices {

    public Subscription save(Subscription subscription);

    public Subscription getById(String id);

    /** Retourne la souscription si elle appartient à userId, sinon lève EntityNotFoundException. */
    public Subscription getByIdAndUser(String id, UUID userId);

    public Page<Subscription> search(String userid, Integer page, Integer size);

    public Page<Subscription> search(String userid, SubscriptionStatus status, Integer page, Integer size);

    public Page<Subscription> search(String userid, String concoursSessionId, Integer page, Integer size);

    public Page<Subscription> search(Integer page, Integer size);

    public Page<Subscription> search(String userid, String concoursSessionId, SubscriptionStatus status, Integer page, Integer size);

    public Page<Subscription> searchBySession(UUID sessionId, Integer page, Integer size);

    public Page<Subscription> searchByConcours(UUID concoursId, Integer page, Integer size);

    public Page<Subscription> searchByUser(UUID userId, Integer page, Integer size);

    public void delete(UUID id);

    /** Supprime la souscription si elle appartient à userId et n'est pas RUNNING. */
    public void deleteByIdAndUser(UUID id, UUID userId);

    boolean hasActiveSubscription(UUID userId, UUID sessionId);

    boolean hasActiveSubscriptionForSessions(UUID userId, List<UUID> sessionIds);

    /** Retourne true si l'utilisateur a déjà une souscription en cours (INITIATE, PENDING ou RUNNING) pour cette session. */
    boolean hasOngoingSubscription(UUID userId, UUID sessionId);

    /** Active la souscription (statut → RUNNING) après confirmation du paiement. */
    void activate(UUID subscriptionId);

    /** Annule la souscription (statut → CANCELED) sans la supprimer. */
    void cancel(UUID subscriptionId);

    /**
     * Retourne la dernière souscription en cours (INITIATE ou PENDING) pour un user + session.
     * Exclut RUNNING intentionnellement : une souscription active ne doit jamais être écrasée.
     */
    Optional<Subscription> findOngoingSubscription(UUID userId, UUID sessionId);

    /** Clôture une souscription dont le paiement a échoué (statut → PAYMENT_FAILED). */
    void markPaymentFailed(UUID subscriptionId);

    /**
     * Marque un achat groupé comme converti en codes d'activation (statut → CODES_ISSUED).
     * La souscription d'achat n'ouvre alors aucun accès : seuls les codes le font.
     */
    void markCodesIssued(UUID subscriptionId);
}
