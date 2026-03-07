package com.mutrix.prepa.infrastructure.mappers.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.PaymentServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {PaymentProviderMapper.class})
public interface PaymentServiceMapper {

    @Mapping(source = "provider.id", target = "paymentProviderId")
    PaymentService toModel(PaymentServiceEntity entity);

    @Mapping(source = "paymentProviderId", target = "provider.id")
    PaymentServiceEntity toEntity(PaymentService model);

    List<PaymentService> toModelList(List<PaymentServiceEntity> entities);
}
