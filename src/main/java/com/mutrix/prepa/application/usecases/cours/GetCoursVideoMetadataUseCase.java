package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.response.cours.VideoMetadataResponse;
import com.mutrix.prepa.cors.AccessDeniedException;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionCoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import com.mutrix.prepa.infrastructure.services.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Métadonnées de la vidéo d'un cours, pour préparer ou reprendre un téléchargement.
 *
 * <p>Le fichier est servi directement par le stockage objet, qui gère les
 * requêtes {@code Range} : le backend n'a pas à relayer les octets. Son rôle ici
 * est de fournir la taille et l'empreinte permettant au client de vérifier
 * qu'un fragment déjà téléchargé correspond toujours au fichier distant.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetCoursVideoMetadataUseCase {

    private final CoursRepository coursRepository;
    private final ConcoursSessionCoursRepository sessionCoursRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MinioService minioService;

    @Transactional(readOnly = true)
    public VideoMetadataResponse execute(UUID coursId, UUID userId) {
        CoursEntity cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable : " + coursId));

        if (cours.getVideoUrl() == null || cours.getVideoUrl().isBlank()) {
            throw new EntityNotFoundException("Ce cours ne contient aucune vidéo.");
        }
        if (!hasAccess(coursId, cours, userId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à ce cours.");
        }

        String objectName = minioService.objectNameFromUrl(cours.getVideoUrl());
        if (objectName == null) {
            throw new EntityNotFoundException("La vidéo n'est pas hébergée sur le stockage courant.");
        }

        MinioService.ObjectStat stat = minioService.objectStat(objectName);

        return VideoMetadataResponse.builder()
                .coursId(coursId)
                .videoUrl(cours.getVideoUrl())
                .size(stat.size())
                .etag(stat.etag())
                .contentType(stat.contentType())
                .hasBeenCrypted(Boolean.TRUE.equals(cours.getHasBeenCrypted()))
                // MinIO expose l'API S3 : les requêtes Range sont supportées.
                .supportsRange(true)
                .build();
    }

    private boolean hasAccess(UUID coursId, CoursEntity cours, UUID userId) {
        if (userId == null) return false;
        if (Boolean.TRUE.equals(cours.getGratuit())) return true;

        List<UUID> sessionIds = sessionCoursRepository.findSessionIdsByCours_Id(coursId);
        if (sessionIds.isEmpty()) return false;

        return subscriptionRepository.existsByUser_IdAndSessions_IdInAndStatus(
                userId, sessionIds, SubscriptionStatus.RUNNING);
    }
}
