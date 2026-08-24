package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.response.cours.VideoKeyResponse;
import com.mutrix.prepa.cors.AccessDeniedException;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.services.ContentKeyService;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionCoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Délivre la clé de déchiffrement d'une vidéo à un utilisateur autorisé.
 *
 * <p>C'est le point de contrôle d'accès réel du dispositif : le conteneur
 * chiffré peut circuler librement depuis le bucket public, il reste illisible
 * sans la clé, et la clé n'est remise qu'à un compte disposant d'un accès actif.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetCoursVideoKeyUseCase {

    private final CoursRepository coursRepository;
    private final ConcoursSessionCoursRepository sessionCoursRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ContentKeyService contentKeyService;

    @Transactional(readOnly = true)
    public VideoKeyResponse execute(UUID coursId, UUID userId) {
        CoursEntity cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable : " + coursId));

        if (!Boolean.TRUE.equals(cours.getHasBeenCrypted())) {
            throw new EntityNotFoundException("Cette vidéo n'est pas chiffrée : aucune clé requise.");
        }

        if (!hasAccess(coursId, cours, userId)) {
            // Message volontairement identique quel que soit le motif : ne pas
            // révéler la composition des sessions à un utilisateur non abonné.
            throw new AccessDeniedException("Vous n'avez pas accès à ce cours.");
        }

        ContentKeyService.ContentKey key = contentKeyService.resolveFor(coursId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune clé enregistrée pour ce cours."));

        return VideoKeyResponse.builder()
                .coursId(coursId)
                .keyId(key.keyId())
                .key(Base64.getEncoder().encodeToString(key.key()))
                .algorithm("AES-256-GCM/CHUNKED")
                .build();
    }

    /**
     * Un cours gratuit est ouvert à tout compte authentifié. Sinon, il faut une
     * souscription RUNNING sur l'une des sessions qui contiennent ce cours.
     */
    private boolean hasAccess(UUID coursId, CoursEntity cours, UUID userId) {
        if (userId == null) return false;
        if (Boolean.TRUE.equals(cours.getGratuit())) return true;

        List<UUID> sessionIds = sessionCoursRepository.findSessionIdsByCours_Id(coursId);
        if (sessionIds.isEmpty()) return false;

        return subscriptionRepository.existsByUser_IdAndSessions_IdInAndStatus(
                userId, sessionIds, SubscriptionStatus.RUNNING);
    }
}
