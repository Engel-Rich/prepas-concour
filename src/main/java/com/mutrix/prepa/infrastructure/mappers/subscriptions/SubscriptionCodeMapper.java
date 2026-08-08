package com.mutrix.prepa.infrastructure.mappers.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionCodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionCodeMapper {

    @Mapping(source = "subscription.id", target = "subscriptionId")
    @Mapping(source = "usedBy.id", target = "usedByUserId")
    @Mapping(source = "activatedSubscription.id", target = "activatedSubscriptionId")
    public SubscriptionCode toModel(SubscriptionCodeEntity entity);

    @Mapping(source = "subscriptionId", target = "subscription.id")
    @Mapping(source = "usedByUserId", target = "usedBy.id")
    @Mapping(source = "activatedSubscriptionId", target = "activatedSubscription.id")
    public SubscriptionCodeEntity toEntity(SubscriptionCode subscriptionCode);
}
