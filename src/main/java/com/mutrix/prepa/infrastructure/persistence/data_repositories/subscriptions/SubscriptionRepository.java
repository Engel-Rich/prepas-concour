package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;

import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
    Page<SubscriptionEntity> findAllByUser_IdAndConcoursSession_Id(String userId, String concoursSessionId, Pageable pageable);

    Page<SubscriptionEntity> findAllByUserId(String userId, Pageable pageable);

    Page<SubscriptionEntity> findAllByUser_IdAndStatus(String userId, SubscriptionStatus status, Pageable pageable);

    Page<SubscriptionEntity> findAllByUser_IdAndConcoursSession_IdAndStatus(String userId, String concoursSessionId, SubscriptionStatus status, Pageable pageable);
}
