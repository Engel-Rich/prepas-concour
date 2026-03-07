package com.mutrix.prepa.infrastructure.mappers.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "subscription.id", target = "subscriptionId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "paymentService.id", target = "paymentServiceId")
    Transaction toModel(TransactionEntity entity);

    @Mapping(source = "subscriptionId", target = "subscription.id")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "paymentServiceId", target = "paymentService.id")
    TransactionEntity toEntity(Transaction model);

    List<Transaction> toModelList(List<TransactionEntity> entities);
}
