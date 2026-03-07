package com.mutrix.prepa.infrastructure.implementations.subscriptions;

import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.mappers.subscriptions.SubscriptionMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImplement implements SubscriptionServices {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionEntity entity = subscriptionMapper.toEntity(subscription);
        return subscriptionMapper.toModel(subscriptionRepository.save(entity));
    }

    @Override
    public Subscription getById(String id) {
        return subscriptionRepository.findById(UUID.fromString(id))
                .map(subscriptionMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("Subscription introuvable avec l'id : " + id));
    }

    @Override
    public Page<Subscription> search(String userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllByUserId(userId, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> search(String userId, SubscriptionStatus status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllByUser_IdAndStatus(userId, status, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> search(String userId, String concoursSessionId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllByUser_IdAndConcoursSession_Id(userId, concoursSessionId, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> search(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAll(pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> search(String userId, String concoursSessionId, SubscriptionStatus status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllByUser_IdAndConcoursSession_IdAndStatus(userId, concoursSessionId, status, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public void delete(UUID id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new EntityNotFoundException("Subscription introuvable avec l'id : " + id);
        }
        subscriptionRepository.deleteById(id);
    }
}