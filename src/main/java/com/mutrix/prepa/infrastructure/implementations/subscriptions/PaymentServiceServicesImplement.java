package com.mutrix.prepa.infrastructure.implementations.subscriptions;


import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.infrastructure.mappers.subscriptions.PaymentServiceMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.PaymentServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentServiceServicesImplement implements PaymentServiceService {
    private final PaymentServiceRepository paymentServiceRepository;
    private final PaymentServiceMapper paymentServiceMapper;


    @Override
    public PaymentService save(PaymentService service) {
        return paymentServiceMapper.toModel(
                paymentServiceRepository.save(
                        paymentServiceMapper.toEntity(service)
                )
        );
    }

    @Override
    public PaymentService getById(UUID id) {
        return paymentServiceMapper.toModel(
                paymentServiceRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Aucun service trouve avec cet Id"))
        );
    }

    @Override
    public List<PaymentService> getByProviderId(UUID id) {
        return paymentServiceRepository.findAllByProvider_id(id).stream().map(paymentServiceMapper::toModel).toList();
    }

    @Override
    public List<PaymentService> getBySens(TransactionSens sens) {
        return paymentServiceRepository.findAllBySens(sens).stream().map(paymentServiceMapper::toModel).toList();
    }

    @Override
    public List<PaymentService> list() {
        return paymentServiceRepository.findAll().stream().map(paymentServiceMapper::toModel).toList();
    }

    @Override
    public void delete(UUID id) {
        paymentServiceRepository.deleteById(id);
    }
}