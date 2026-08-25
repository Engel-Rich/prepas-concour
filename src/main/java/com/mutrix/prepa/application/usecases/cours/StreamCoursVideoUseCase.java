package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.services.ContentKeyService;
import com.mutrix.prepa.infrastructure.crypto.VideoStreamDecryptor;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import com.mutrix.prepa.infrastructure.services.MinioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Prévisualisation d'une vidéo depuis la console d'administration.
 *
 * <p>Le serveur déchiffre à la volée et renvoie du MP4 en clair, avec support
 * des requêtes {@code Range} : la balise {@code <video>} du navigateur peut
 * démarrer, se déplacer dans la vidéo et ne charger que ce qu'elle affiche.
 *
 * <p>La clé de contenu ne quitte jamais le serveur — contrairement au mobile,
 * qui la reçoit pour lire hors connexion. Un navigateur n'offre pas de coffre
 * comparable au Keychain, et une clé remise au JavaScript serait lisible dans
 * les outils de développement.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamCoursVideoUseCase {

    private final CoursRepository coursRepository;
    private final ContentKeyService contentKeyService;
    private final VideoStreamDecryptor decryptor;
    private final MinioService minioService;

    /** Description de la vidéo servie, avant écriture du corps. */
    public record StreamInfo(long totalSize, String contentType, boolean encrypted, String objectName) {}

    @Transactional(readOnly = true)
    public StreamInfo describe(UUID coursId) {
        CoursEntity cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable : " + coursId));

        if (cours.getVideoUrl() == null || cours.getVideoUrl().isBlank()) {
            throw new EntityNotFoundException("Ce cours ne contient aucune vidéo.");
        }

        String objectName = minioService.objectNameFromUrl(cours.getVideoUrl());
        if (objectName == null) {
            throw new EntityNotFoundException("Vidéo absente du stockage courant.");
        }

        boolean encrypted = Boolean.TRUE.equals(cours.getHasBeenCrypted());

        if (!encrypted) {
            MinioService.ObjectStat stat = minioService.objectStat(objectName);
            String type = stat.contentType() != null ? stat.contentType() : "video/mp4";
            return new StreamInfo(stat.size(), type, false, objectName);
        }

        VideoStreamDecryptor.Header header = decryptor.readHeader(objectName);
        // La taille annoncée est celle du clair : c'est elle que le lecteur
        // utilise pour construire sa barre de progression.
        return new StreamInfo(header.plaintextSize(), "video/mp4", true, objectName);
    }

    /** Écrit la plage demandée dans la réponse, en clair. */
    @Transactional(readOnly = true)
    public void writeRange(UUID coursId, StreamInfo info, long from, long to,
                           HttpServletResponse response) throws Exception {

        try (OutputStream out = response.getOutputStream()) {
            if (!info.encrypted()) {
                long length = to - from + 1;
                try (InputStream in = minioService.openRange(info.objectName(), from, length)) {
                    in.transferTo(out);
                }
                out.flush();
                return;
            }

            ContentKeyService.ContentKey contentKey = contentKeyService.resolveFor(coursId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Vidéo chiffrée sans clé enregistrée — re-chiffrez le cours."));

            VideoStreamDecryptor.Header header = decryptor.readHeader(info.objectName());
            decryptor.streamRange(info.objectName(), contentKey.key(), header, from, to, out);
        }
    }
}
