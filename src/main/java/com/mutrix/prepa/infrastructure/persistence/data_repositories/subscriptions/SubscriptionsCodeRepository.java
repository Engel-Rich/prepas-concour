package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;


import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionsCodeRepository extends JpaRepository<SubscriptionCodeEntity, UUID> {
    Optional<SubscriptionCodeEntity> findBySubscription_Id(UUID id);
}
