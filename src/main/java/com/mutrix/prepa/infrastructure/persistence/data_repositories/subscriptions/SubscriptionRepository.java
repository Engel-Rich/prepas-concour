package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;

import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
    Page<SubscriptionEntity> findAllByUser_IdAndSessions_Id(UUID userId, UUID concoursSessionId, Pageable pageable);

    Page<SubscriptionEntity> findAllByUser_Id(UUID userId, Pageable pageable);

    Page<SubscriptionEntity> findAllByUser_IdAndStatus(UUID userId, SubscriptionStatus status, Pageable pageable);

    Page<SubscriptionEntity> findAllByUser_IdAndSessions_IdAndStatus(UUID userId, UUID concoursSessionId, SubscriptionStatus status, Pageable pageable);

    // By session
    Page<SubscriptionEntity> findAllBySessions_Id(UUID sessionId, Pageable pageable);

    // By concours (via session → concours)
    Page<SubscriptionEntity> findAllBySessions_Concours_Id(UUID concoursId, Pageable pageable);

    long countByStatus(SubscriptionStatus status);

    long countByCreatedAtAfter(LocalDateTime date);

    /** Concours distincts auxquels un utilisateur est abonné (via ses sessions). */
    @Query("SELECT DISTINCT sub.sessions.concours FROM SubscriptionEntity sub WHERE sub.user.id = :userId")
    List<ConcoursEntity> findDistinctConcoursByUserId(@Param("userId") UUID userId);
}
