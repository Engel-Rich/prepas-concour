package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.dto.response.cours.VideoEncryptionResponse;
import com.mutrix.prepa.application.dto.response.cours.VideoEncryptionResponse.Outcome;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.infrastructure.mappers.CoursEntityMapper;
import com.mutrix.prepa.domaines.services.ContentKeyService;
import com.mutrix.prepa.domaines.services.VideoCipherService;
import com.mutrix.prepa.infrastructure.crypto.EnvelopeContentKeyService;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import com.mutrix.prepa.infrastructure.services.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Chiffre la vidéo d'un cours et remplace l'objet stocké.
 *
 * <p><b>Principe.</b> Le contenu clair est lu en flux, chiffré bloc par bloc et
 * réécrit directement vers le bucket : à aucun moment la vidéo entière n'est
 * chargée en mémoire ni écrite en clair sur le disque du serveur.
 *
 * <p><b>Tolérance à l'échec.</b> Un échec de chiffrement ne bloque jamais le
 * dépôt d'une vidéo : le fichier reste en clair, {@code hasBeenCrypted} vaut
 * {@code false} et le motif est conservé dans {@code encryptionError} pour la
 * console d'administration. L'opération reste rejouable.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EncryptCoursVideoUseCase {

    private final CoursRepository coursRepository;
    private final MinioService minioService;
    private final VideoCipherService videoCipherService;
    private final ContentKeyService contentKeyService;
    private final EnvelopeContentKeyService envelopeContentKeyService;

    /**
     * Chiffre une vidéo déjà présente dans le bucket (re-chiffrement à la
     * demande depuis la console d'administration).
     */
    @Transactional
    public VideoEncryptionResponse execute(UUID coursId) {
        CoursEntity cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable : " + coursId));

        if (cours.getVideoUrl() == null || cours.getVideoUrl().isBlank()) {
            return response(Outcome.NO_VIDEO, "Ce cours ne contient aucune vidéo.",
                    null, null, cours);
        }
        if (Boolean.TRUE.equals(cours.getHasBeenCrypted())) {
            return response(Outcome.ALREADY_ENCRYPTED, "Cette vidéo est déjà chiffrée.",
                    null, null, cours);
        }
        if (!envelopeContentKeyService.isAvailable()) {
            return failure(cours, "CONFIGURATION",
                    "Clé maîtresse de chiffrement absente sur le serveur.");
        }

        String sourceObject = minioService.objectNameFromUrl(cours.getVideoUrl());
        if (sourceObject == null) {
            return failure(cours, "RESOLUTION",
                    "L'URL de la vidéo ne correspond pas au bucket courant.");
        }

        String stage = "READ";
        try {
            long plaintextSize = minioService.objectSize(sourceObject);

            stage = "KEY";
            ContentKeyService.ContentKey contentKey = contentKeyService.issueFor(coursId);

            stage = "ENCRYPT";
            String targetObject = encryptedObjectName(sourceObject);
            String newUrl;
            try (InputStream source = minioService.openStream(sourceObject)) {
                newUrl = pipeEncryptedToBucket(source, targetObject, contentKey,
                        plaintextSize);
            }

            stage = "PERSIST";
            cours.setVideoUrl(newUrl);
            cours.setHasBeenCrypted(true);
            cours.setEncryptionError(null);
            coursRepository.save(cours);

            // L'original en clair ne doit pas survivre dans le bucket public.
            minioService.deleteQuietly(sourceObject);

            log.info("[Crypto] Vidéo du cours {} chiffrée : {} -> {}", coursId, sourceObject, targetObject);
            return response(Outcome.ENCRYPTED, "Vidéo chiffrée avec succès.", null, null, cours);

        } catch (Exception e) {
            log.error("[Crypto] Échec du chiffrement du cours {} à l'étape {} : {}",
                    coursId, stage, e.getMessage(), e);
            return failure(cours, stage, e.getMessage());
        }
    }

    /**
     * Dépose une vidéo sur un cours existant : chiffrement si possible, puis
     * enregistrement de l'URL et de l'état obtenu. L'ancienne vidéo est retirée
     * du bucket.
     */
    @Transactional
    public CoursResponse attachVideo(UUID coursId, MultipartFile file) {
        CoursEntity cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable : " + coursId));

        String previousObject = minioService.objectNameFromUrl(cours.getVideoUrl());

        UploadResult result = encryptOnUpload(file, coursId);

        cours.setVideoUrl(result.videoUrl());
        cours.setHasBeenCrypted(result.encrypted());
        cours.setEncryptionError(result.error());
        coursRepository.save(cours);

        if (previousObject != null) {
            minioService.deleteQuietly(previousObject);
        }

        return CoursResponse.fromDomain(CoursEntityMapper.toDCoursDomain(cours), true);
    }

    /**
     * Chiffre un fichier fraîchement déposé, avant son enregistrement en base.
     * Renvoie l'URL de l'objet stocké et l'état de chiffrement obtenu.
     *
     * @param coursId identifiant du cours auquel rattacher la clé
     */
    public UploadResult encryptOnUpload(MultipartFile file, UUID coursId) {
        if (!envelopeContentKeyService.isAvailable()) {
            log.warn("[Crypto] Chiffrement indisponible — dépôt en clair pour le cours {}", coursId);
            return new UploadResult(minioService.uploadFile(file, "cours"), false,
                    "Clé maîtresse de chiffrement absente sur le serveur.");
        }

        String stage = "KEY";
        try {
            ContentKeyService.ContentKey contentKey = contentKeyService.issueFor(coursId);

            stage = "ENCRYPT";
            String filename = file.getOriginalFilename() != null
                    ? file.getOriginalFilename() : "video";
            String targetObject = minioService.buildObjectName("cours", filename + ".mxv");

            String url;
            try (InputStream source = file.getInputStream()) {
                url = pipeEncryptedToBucket(source, targetObject, contentKey, file.getSize());
            }
            return new UploadResult(url, true, null);

        } catch (Exception e) {
            // Le dépôt ne doit jamais être bloqué par un échec de chiffrement.
            log.error("[Crypto] Chiffrement impossible à l'étape {} — dépôt en clair : {}",
                    stage, e.getMessage(), e);
            contentKeyService.revokeFor(coursId);
            return new UploadResult(minioService.uploadFile(file, "cours"), false,
                    stage + " : " + e.getMessage());
        }
    }

    /** Résultat d'un dépôt : URL stockée, état de chiffrement, motif d'échec éventuel. */
    public record UploadResult(String videoUrl, boolean encrypted, String error) {}

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Relie le chiffreur au client MinIO par un tube.
     *
     * <p>MinIO consomme un {@code InputStream}, le chiffreur produit vers un
     * {@code OutputStream} : le tube évite le fichier temporaire intermédiaire.
     * Le chiffrement s'exécute dans un autre fil, sinon le tube se bloquerait
     * dès que son tampon serait plein.
     */
    private String pipeEncryptedToBucket(InputStream source, String targetObject,
                                         ContentKeyService.ContentKey contentKey,
                                         long plaintextSize) throws Exception {

        long encryptedSize = videoCipherService.encryptedSizeFor(plaintextSize);

        try (PipedInputStream pipeIn = new PipedInputStream(64 * 1024);
             PipedOutputStream pipeOut = new PipedOutputStream(pipeIn)) {

            CompletableFuture<Void> cipherTask = CompletableFuture.runAsync(() -> {
                try (PipedOutputStream out = pipeOut) {
                    videoCipherService.encrypt(source, out, contentKey.key(),
                            contentKey.keyId(), plaintextSize);
                } catch (Exception e) {
                    throw new IllegalStateException("Chiffrement interrompu : " + e.getMessage(), e);
                }
            });

            String url = minioService.uploadStream(pipeIn, targetObject, encryptedSize,
                    "application/octet-stream");

            // Propage une éventuelle erreur du fil de chiffrement.
            cipherTask.join();
            return url;
        }
    }

    private String encryptedObjectName(String sourceObject) {
        return sourceObject + ".mxv";
    }

    private VideoEncryptionResponse failure(CoursEntity cours, String stage, String detail) {
        cours.setHasBeenCrypted(false);
        cours.setEncryptionError(stage + " : " + detail);
        coursRepository.save(cours);
        return response(Outcome.FAILED,
                "Le chiffrement a échoué. La vidéo reste accessible en clair.",
                stage, detail, cours);
    }

    private VideoEncryptionResponse response(Outcome outcome, String message,
                                             String stage, String detail, CoursEntity cours) {
        return VideoEncryptionResponse.builder()
                .outcome(outcome)
                .message(message)
                .stage(stage)
                .errorDetail(detail)
                .coursId(cours.getId())
                .hasBeenCrypted(Boolean.TRUE.equals(cours.getHasBeenCrypted()))
                .videoUrl(cours.getVideoUrl())
                .build();
    }
}
