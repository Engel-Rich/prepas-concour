package com.mutrix.prepa.infrastructure.implementations.subscriptions;

import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.infrastructure.mappers.subscriptions.SubscriptionCodeMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionsCodeRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionCodeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionCodeServiceImplement implements SubscriptionCodeServices {

    private final SubscriptionsCodeRepository subscriptionsCodeRepository;
    private final SubscriptionCodeMapper subscriptionCodeMapper;

    @Override
    public SubscriptionCode save(SubscriptionCode code) {
        SubscriptionCodeEntity entity = subscriptionCodeMapper.toEntity(code);
        return subscriptionCodeMapper.toModel(subscriptionsCodeRepository.save(entity));
    }

    @Override
    public SubscriptionCode getById(UUID id) {
        return subscriptionsCodeRepository.findById(id)
                .map(subscriptionCodeMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("SubscriptionCode introuvable avec l'id : " + id));
    }

    @Override
    public SubscriptionCode getBySubscriptionId(UUID id) {
        return subscriptionsCodeRepository.findBySubscription_Id(id)
                .map(subscriptionCodeMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("SubscriptionCode introuvable pour la subscription : " + id));
    }

    @Override
    public void delete(UUID id) {
        if (!subscriptionsCodeRepository.existsById(id)) {
            throw new EntityNotFoundException("SubscriptionCode introuvable avec l'id : " + id);
        }
        subscriptionsCodeRepository.deleteById(id);
    }
}