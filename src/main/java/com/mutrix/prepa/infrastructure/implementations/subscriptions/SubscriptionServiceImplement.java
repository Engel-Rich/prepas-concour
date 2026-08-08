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

import java.util.List;
import java.util.Optional;
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
        return subscriptionRepository.findAllByUser_Id(UUID.fromString(userId), pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> search(String userId, SubscriptionStatus status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllByUser_IdAndStatus(UUID.fromString(userId), status, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> search(String userId, String concoursSessionId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllByUser_IdAndSessions_Id(
                UUID.fromString(userId), UUID.fromString(concoursSessionId), pageable)
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
        return subscriptionRepository.findAllByUser_IdAndSessions_IdAndStatus(
                UUID.fromString(userId), UUID.fromString(concoursSessionId), status, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> searchBySession(UUID sessionId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllBySessions_Id(sessionId, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> searchByConcours(UUID concoursId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllBySessions_Concours_Id(concoursId, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Page<Subscription> searchByUser(UUID userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return subscriptionRepository.findAllByUser_Id(userId, pageable)
                .map(subscriptionMapper::toModel);
    }

    @Override
    public Subscription getByIdAndUser(String id, UUID userId) {
        SubscriptionEntity entity = subscriptionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable avec l'id : " + id));
        if (!entity.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Souscription introuvable avec l'id : " + id);
        }
        return subscriptionMapper.toModel(entity);
    }

    @Override
    public void delete(UUID id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new EntityNotFoundException("Subscription introuvable avec l'id : " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    @Override
    public void deleteByIdAndUser(UUID id, UUID userId) {
        SubscriptionEntity entity = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable avec l'id : " + id));
        if (!entity.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Souscription introuvable avec l'id : " + id);
        }
        if (entity.getStatus() == com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus.RUNNING) {
            throw new IllegalStateException("Impossible de supprimer une souscription active");
        }
        subscriptionRepository.deleteById(id);
    }

    @Override
    public boolean hasActiveSubscription(UUID userId, UUID sessionId) {
        return subscriptionRepository.existsByUser_IdAndSessions_IdAndStatus(
                userId, sessionId, SubscriptionStatus.RUNNING);
    }

    @Override
    public boolean hasActiveSubscriptionForSessions(UUID userId, List<UUID> sessionIds) {
        return subscriptionRepository.existsByUser_IdAndSessions_IdInAndStatus(
                userId, sessionIds, SubscriptionStatus.RUNNING);
    }

    @Override
    public boolean hasOngoingSubscription(UUID userId, UUID sessionId) {
        return subscriptionRepository.existsByUser_IdAndSessions_IdAndStatusIn(
                userId, sessionId,
                List.of(SubscriptionStatus.INITIATE, SubscriptionStatus.PENDING, SubscriptionStatus.RUNNING));
    }

    @Override
    public void activate(UUID subscriptionId) {
        Subscription sub = getById(subscriptionId.toString());
        sub.setStatus(SubscriptionStatus.RUNNING);
        save(sub);
    }

    @Override
    public void cancel(UUID subscriptionId) {
        Subscription sub = getById(subscriptionId.toString());
        sub.setStatus(SubscriptionStatus.CANCELED);
        save(sub);
    }

    @Override
    public Optional<Subscription> findOngoingSubscription(UUID userId, UUID sessionId) {
        return subscriptionRepository.findFirstByUser_IdAndSessions_IdAndStatusInOrderByCreatedAtDesc(
                        userId, sessionId,
                        List.of(SubscriptionStatus.INITIATE, SubscriptionStatus.PENDING))
                .map(subscriptionMapper::toModel);
    }

    @Override
    public void markPaymentFailed(UUID subscriptionId) {
        Subscription sub = getById(subscriptionId.toString());
        // Un webhook d'échec livré en retard ne doit jamais révoquer un accès
        // déjà payé et honoré (activation ou codes émis).
        if (sub.getStatus() == SubscriptionStatus.RUNNING
                || sub.getStatus() == SubscriptionStatus.CODES_ISSUED) {
            return;
        }
        sub.setStatus(SubscriptionStatus.PAYMENT_FAILED);
        save(sub);
    }

    @Override
    public void markCodesIssued(UUID subscriptionId) {
        Subscription sub = getById(subscriptionId.toString());
        sub.setStatus(SubscriptionStatus.CODES_ISSUED);
        save(sub);
    }
}