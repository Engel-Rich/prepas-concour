package com.mutrix.prepa.infrastructure.implementations.subscriptions;

import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import com.mutrix.prepa.infrastructure.mappers.subscriptions.SubscriptionCodeMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionsCodeRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.UserRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionCodeEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionCodeServiceImplement implements SubscriptionCodeServices {

    /** Alphabet sans caractères ambigus (0/O, 1/I/L) pour une saisie manuelle fiable. */
    private static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int GROUP_SIZE = 4;
    private static final int GROUPS = 2;
    private static final int MAX_GENERATION_ATTEMPTS = 20;

    private final SecureRandom random = new SecureRandom();

    private final SubscriptionsCodeRepository subscriptionsCodeRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
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
    @Transactional
    public List<SubscriptionCode> generateForSubscription(UUID subscriptionId, int quantity) {
        SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable : " + subscriptionId));

        List<SubscriptionCodeEntity> created = new ArrayList<>(Math.max(quantity, 0));
        for (int i = 0; i < quantity; i++) {
            SubscriptionCodeEntity entity = SubscriptionCodeEntity.builder()
                    .code(nextUniqueCode())
                    .status(CodeStatus.ACTIVE)
                    .subscription(subscription)
                    .isActive(true)
                    .build();
            created.add(subscriptionsCodeRepository.save(entity));
        }
        return created.stream().map(subscriptionCodeMapper::toModel).toList();
    }

    @Override
    public Optional<SubscriptionCode> findByCode(String code) {
        return subscriptionsCodeRepository.findByCode(normalize(code))
                .map(subscriptionCodeMapper::toModel);
    }

    @Override
    public List<SubscriptionCode> findAllBySubscriptionId(UUID subscriptionId) {
        return subscriptionsCodeRepository.findAllBySubscription_IdOrderByCreatedAtAsc(subscriptionId)
                .stream().map(subscriptionCodeMapper::toModel).toList();
    }

    @Override
    public List<SubscriptionCode> findAllByBuyer(UUID buyerId) {
        return subscriptionsCodeRepository.findAllBySubscription_User_IdOrderByCreatedAtDesc(buyerId)
                .stream().map(subscriptionCodeMapper::toModel).toList();
    }

    @Override
    public Page<SubscriptionCode> searchByBuyer(UUID buyerId, Integer page, Integer size) {
        return subscriptionsCodeRepository.findAllBySubscription_User_Id(buyerId, pageable(page, size))
                .map(subscriptionCodeMapper::toModel);
    }

    @Override
    public Page<SubscriptionCode> searchByBuyer(UUID buyerId, CodeStatus status, Integer page, Integer size) {
        return subscriptionsCodeRepository
                .findAllBySubscription_User_IdAndStatus(buyerId, status, pageable(page, size))
                .map(subscriptionCodeMapper::toModel);
    }

    @Override
    public Page<SubscriptionCode> search(Integer page, Integer size) {
        return subscriptionsCodeRepository.findAll(pageable(page, size))
                .map(subscriptionCodeMapper::toModel);
    }

    @Override
    public Page<SubscriptionCode> search(CodeStatus status, Integer page, Integer size) {
        return subscriptionsCodeRepository.findAllByStatus(status, pageable(page, size))
                .map(subscriptionCodeMapper::toModel);
    }

    @Override
    public Page<SubscriptionCode> searchBySession(UUID sessionId, Integer page, Integer size) {
        return subscriptionsCodeRepository.findAllBySubscription_Sessions_Id(sessionId, pageable(page, size))
                .map(subscriptionCodeMapper::toModel);
    }

    @Override
    public Page<SubscriptionCode> searchByConcours(UUID concoursId, Integer page, Integer size) {
        return subscriptionsCodeRepository.findAllBySubscription_Sessions_Concours_Id(concoursId, pageable(page, size))
                .map(subscriptionCodeMapper::toModel);
    }

    @Override
    @Transactional
    public SubscriptionCode consume(UUID codeId, UUID usedByUserId, UUID activatedSubscriptionId) {
        SubscriptionCodeEntity entity = subscriptionsCodeRepository.findById(codeId)
                .orElseThrow(() -> new EntityNotFoundException("SubscriptionCode introuvable avec l'id : " + codeId));

        // Garde-fou : un code n'est consommable qu'une seule fois
        if (entity.getStatus() != CodeStatus.ACTIVE || entity.getUsedBy() != null) {
            throw new IllegalStateException("Ce code a déjà été utilisé ou n'est plus valide");
        }

        entity.setStatus(CodeStatus.USED);
        entity.setUsedAt(LocalDateTime.now());
        entity.setUsedBy(userRepository.findById(usedByUserId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable : " + usedByUserId)));
        entity.setActivatedSubscription(subscriptionRepository.findById(activatedSubscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Souscription introuvable : " + activatedSubscriptionId)));

        return subscriptionCodeMapper.toModel(subscriptionsCodeRepository.save(entity));
    }

    @Override
    @Transactional
    public SubscriptionCode release(UUID codeId, String reason) {
        SubscriptionCodeEntity entity = requireCode(codeId);
        entity.setStatus(CodeStatus.ACTIVE);
        entity.setUsedBy(null);
        entity.setUsedAt(null);
        entity.setActivatedSubscription(null);
        traceRepair(entity, "RELEASED", reason);
        return subscriptionCodeMapper.toModel(subscriptionsCodeRepository.save(entity));
    }

    @Override
    @Transactional
    public SubscriptionCode invalidate(UUID codeId, String reason) {
        SubscriptionCodeEntity entity = requireCode(codeId);
        entity.setStatus(CodeStatus.INVALID);
        traceRepair(entity, "INVALIDATED", reason);
        return subscriptionCodeMapper.toModel(subscriptionsCodeRepository.save(entity));
    }

    @Override
    @Transactional
    public SubscriptionCode markExpired(UUID codeId, String reason) {
        SubscriptionCodeEntity entity = requireCode(codeId);
        entity.setStatus(CodeStatus.EXPIRED);
        traceRepair(entity, "EXPIRED", reason);
        return subscriptionCodeMapper.toModel(subscriptionsCodeRepository.save(entity));
    }

    private SubscriptionCodeEntity requireCode(UUID codeId) {
        return subscriptionsCodeRepository.findById(codeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "SubscriptionCode introuvable avec l'id : " + codeId));
    }

    /** Conserve l'historique des interventions dans les métadonnées du code. */
    @SuppressWarnings("unchecked")
    private void traceRepair(SubscriptionCodeEntity entity, String action, String reason) {
        try {
            Map<String, Object> metadata = entity.getMetadata() == null
                    ? new HashMap<>()
                    : new HashMap<>(entity.getMetadata());

            List<Object> history = metadata.get("repairHistory") instanceof List
                    ? new ArrayList<>((List<Object>) metadata.get("repairHistory"))
                    : new ArrayList<>();

            history.add(Map.of(
                    "action", action,
                    "reason", reason != null ? reason : "",
                    "at", LocalDateTime.now().toString()));

            metadata.put("repairHistory", history);
            entity.setMetadata(metadata);
        } catch (Exception ignored) {
            // La trace d'audit ne doit jamais faire échouer la réparation
        }
    }

    @Override
    public long countRemaining(UUID subscriptionId) {
        return subscriptionsCodeRepository.countBySubscription_IdAndStatus(subscriptionId, CodeStatus.ACTIVE);
    }

    @Override
    public void delete(UUID id) {
        if (!subscriptionsCodeRepository.existsById(id)) {
            throw new EntityNotFoundException("SubscriptionCode introuvable avec l'id : " + id);
        }
        subscriptionsCodeRepository.deleteById(id);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Pageable pageable(Integer page, Integer size) {
        return PageRequest.of(page, size, Sort.by("createdAt").descending());
    }

    /** Normalise la saisie utilisateur : majuscules, sans espaces ni tirets. */
    public static String normalize(String raw) {
        if (raw == null) return null;
        return raw.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    /** Format lisible : XXXX-XXXX (stocké sans tiret pour comparaison directe). */
    private String nextUniqueCode() {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String candidate = randomCode();
            if (!subscriptionsCodeRepository.existsByCode(candidate)) return candidate;
        }
        throw new IllegalStateException("Impossible de générer un code unique après "
                + MAX_GENERATION_ATTEMPTS + " tentatives");
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(GROUP_SIZE * GROUPS);
        for (int i = 0; i < GROUP_SIZE * GROUPS; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
