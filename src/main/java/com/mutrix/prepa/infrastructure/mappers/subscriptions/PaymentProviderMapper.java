package com.mutrix.prepa.infrastructure.mappers.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.PaymentProviderEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentProviderMapper {

    PaymentProvider toModel(PaymentProviderEntity entity);

    PaymentProviderEntity toEntity(PaymentProvider model);

    List<PaymentProvider> toModelList(List<PaymentProviderEntity> entities);
}
