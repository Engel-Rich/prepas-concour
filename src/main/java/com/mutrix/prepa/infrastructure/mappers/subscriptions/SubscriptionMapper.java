package com.mutrix.prepa.infrastructure.mappers.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(source = "sessions.id", target = "concoursSessionId")
    @Mapping(source = "users.id", target = "userId")
    Subscription toModel(SubscriptionEntity entity);

    @Mapping(source = "concoursSessionId", target = "sessions.id")
    @Mapping(source = "userId", target = "users.id")
    SubscriptionEntity toEntity(Subscription model);

    List<Subscription> toModelList(List<SubscriptionEntity> entities);
}
