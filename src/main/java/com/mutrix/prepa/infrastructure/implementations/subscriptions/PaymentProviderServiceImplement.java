package com.mutrix.prepa.infrastructure.implementations.subscriptions;

import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import com.mutrix.prepa.infrastructure.mappers.subscriptions.PaymentProviderMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.PaymentProviderRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.PaymentProviderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PaymentProviderServiceImplement implements PaymentProviderService {

    private final PaymentProviderRepository paymentProviderRepository;
    private final PaymentProviderMapper paymentProviderMapper;

    @Override
    public PaymentProvider save(PaymentProvider provider) {
        PaymentProviderEntity entity = paymentProviderMapper.toEntity(provider);
        return paymentProviderMapper.toModel(paymentProviderRepository.save(entity));
    }

    @Override
    public PaymentProvider getByID(UUID id) {
        return paymentProviderRepository.findById(id)
                .map(paymentProviderMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("PaymentProvider introuvable avec l'id : " + id));
    }

    @Override
    public PaymentProvider getByName(UUID name) {
        // Si "name" est bien un UUID ici, c'est probablement un bug dans l'interface
        // À adapter selon votre besoin réel (String name ?)
        return paymentProviderRepository.findByName(name.toString())
                .map(paymentProviderMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("PaymentProvider introuvable avec le nom : " + name));
    }

    @Override
    public List<PaymentProvider> list() {
        return paymentProviderRepository.findAll()
                .stream()
                .map(paymentProviderMapper::toModel)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        if (!paymentProviderRepository.existsById(id)) {
            throw new EntityNotFoundException("PaymentProvider introuvable avec l'id : " + id);
        }
        paymentProviderRepository.deleteById(id);
    }
}