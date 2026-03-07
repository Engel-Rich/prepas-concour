package com.mutrix.prepa.infrastructure.mappers.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionCodeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionCodeMapper {

    @Mapping(source = "subscription.id",target = "subscriptionId")
   public SubscriptionCode toModel(SubscriptionCodeEntity entity);

    @Mapping(source = "subscriptionId", target = "subscription.id")
   public SubscriptionCodeEntity toEntity(SubscriptionCode subscriptionCode);
}
