package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;


import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionsCodeRepository extends JpaRepository<SubscriptionCodeEntity, UUID> {

    Optional<SubscriptionCodeEntity> findBySubscription_Id(UUID id);

    Optional<SubscriptionCodeEntity> findByCode(String code);

    boolean existsByCode(String code);

    /** Tous les codes générés par une souscription d'achat. */
    List<SubscriptionCodeEntity> findAllBySubscription_IdOrderByCreatedAtAsc(UUID subscriptionId);

    // ── Côté acheteur ─────────────────────────────────────────────────────────

    List<SubscriptionCodeEntity> findAllBySubscription_User_IdOrderByCreatedAtDesc(UUID buyerId);

    Page<SubscriptionCodeEntity> findAllBySubscription_User_Id(UUID buyerId, Pageable pageable);

    Page<SubscriptionCodeEntity> findAllBySubscription_User_IdAndStatus(
            UUID buyerId, CodeStatus status, Pageable pageable);

    /** Codes achetés par un utilisateur pour une session donnée. */
    List<SubscriptionCodeEntity> findAllBySubscription_User_IdAndSubscription_Sessions_IdOrderByCreatedAtAsc(
            UUID buyerId, UUID sessionId);

    // ── Côté administration ───────────────────────────────────────────────────

    Page<SubscriptionCodeEntity> findAllByStatus(CodeStatus status, Pageable pageable);

    Page<SubscriptionCodeEntity> findAllBySubscription_Sessions_Id(UUID sessionId, Pageable pageable);

    Page<SubscriptionCodeEntity> findAllBySubscription_Sessions_Concours_Id(UUID concoursId, Pageable pageable);

    long countBySubscription_IdAndStatus(UUID subscriptionId, CodeStatus status);

    long countByStatus(CodeStatus status);
}
