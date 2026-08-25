package com.mutrix.prepa.infrastructure.services;

import io.minio.*;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.public-url}")
    private String minioPublicUrl;

    /**
     * Policy S3 qui autorise la lecture publique (GET) sur tous les objets du bucket.
     * Équivalent de "public-read" dans la console MinIO.
     */
    private String publicReadPolicy(String bucket) {
        return """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action":    ["s3:GetObject"],
                      "Resource":  ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);
    }

    /**
     * Applique la policy publique au démarrage de l'application.
     * Rend immédiatement accessibles toutes les images déjà stockées dans MinIO.
     */
    @PostConstruct
    public void initBucket() {
        try {
            ensureBucketPublic();
            log.info("MinIO : bucket '{}' initialisé et accessible en lecture publique.", bucketName);
        } catch (Exception e) {
            log.error("MinIO : impossible d'initialiser le bucket '{}' : {}", bucketName, e.getMessage());
        }
    }

    /**
     * S'assure que le bucket existe et qu'il est accessible en lecture publique.
     * Appelé avant chaque upload pour garantir la cohérence.
     */
    private void ensureBucketPublic() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build());

        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket '{}' créé.", bucketName);
        }

        // Applique (ou ré-applique) la policy publique en lecture
        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(publicReadPolicy(bucketName))
                        .build());
        log.debug("Policy publique appliquée sur le bucket '{}'.", bucketName);
    }

    /**
     * Upload a file to MinIO inside the given folder and return its public URL.
     *
     * @param file   the multipart file to upload
     * @param folder the subfolder inside the bucket (e.g. "cours", "matieres", "concours")
     * @return the public URL of the stored object
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            ensureBucketPublic();

            String originalFilename = file.getOriginalFilename() != null
                    ? file.getOriginalFilename()
                    : "fichier";

            // Sanitize folder name and build object path: folder/uuid-filename
            String safeFolder = (folder != null && !folder.isBlank())
                    ? folder.replaceAll("[^a-zA-Z0-9_\\-]", "_").toLowerCase()
                    : "uploads";
            String objectName = safeFolder + "/" + UUID.randomUUID() + "-" + originalFilename;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

//            String url = minioPublicUrl + "/" + bucketName + "/" + objectName;
            String publicBaseUrl = minioPublicUrl.replaceAll("/+$", "");
            String url = publicBaseUrl + "/" + bucketName + "/" + objectName;
            log.info("Fichier uploadé sur MinIO : {}", url);
            return url;

        } catch (Exception e) {
            log.error("Erreur upload MinIO : {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'upload du fichier vers MinIO : " + e.getMessage(), e);
        }
    }

    /**
     * Convenience overload — uploads to the "uploads" folder.
     * @deprecated Prefer {@link #uploadFile(MultipartFile, String)} with an explicit folder.
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, "uploads");
    }

    // ── Flux (vidéos chiffrées) ───────────────────────────────────────────────

    /**
     * Dépose un flux sans le charger en mémoire.
     *
     * <p>Utilisé pour les conteneurs vidéo chiffrés : une vidéo de plusieurs
     * centaines de mégaoctets ne doit jamais transiter par un tableau d'octets.
     * {@code putObject} consomme le flux par parties de 10 Mio.
     *
     * @param objectName chemin complet de l'objet dans le bucket
     * @param size       taille exacte du flux, ou -1 si inconnue
     * @return URL publique de l'objet stocké
     */
    public String uploadStream(InputStream stream, String objectName, long size, String contentType) {
        try {
            ensureBucketPublic();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(stream, size, size < 0 ? 10L * 1024 * 1024 : -1)
                            .contentType(contentType != null ? contentType : "application/octet-stream")
                            .build());

            String publicBaseUrl = minioPublicUrl.replaceAll("/+$", "");
            String url = publicBaseUrl + "/" + bucketName + "/" + objectName;
            log.info("Flux uploadé sur MinIO : {} ({} octets)", url, size);
            return url;

        } catch (Exception e) {
            log.error("Erreur upload flux MinIO ({}) : {}", objectName, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'upload du flux vers MinIO : " + e.getMessage(), e);
        }
    }

    /**
     * Ouvre un objet existant en lecture. L'appelant referme le flux.
     * Le nom d'objet est déduit d'une URL publique produite par ce service.
     */
    public InputStream openStream(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.error("Erreur lecture MinIO ({}) : {}", objectName, e.getMessage());
            throw new RuntimeException("Objet introuvable sur MinIO : " + objectName, e);
        }
    }

    /** Taille d'un objet, utile pour dimensionner le conteneur chiffré. */
    /**
     * Ouvre une plage d'octets d'un objet.
     *
     * <p>Permet de ne rapatrier que les blocs nécessaires au lieu du fichier
     * entier — indispensable pour prévisualiser une vidéo de plusieurs
     * centaines de mégaoctets sans la charger côté serveur.
     */
    public InputStream openRange(String objectName, long offset, long length) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .offset(offset)
                            .length(length)
                            .build());
        } catch (Exception e) {
            log.error("Erreur lecture partielle MinIO ({} @{}+{}) : {}",
                    objectName, offset, length, e.getMessage());
            throw new RuntimeException("Impossible de lire la plage demandée : " + objectName, e);
        }
    }

    public long objectSize(String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return stat.size();
        } catch (Exception e) {
            log.error("Erreur stat MinIO ({}) : {}", objectName, e.getMessage());
            throw new RuntimeException("Impossible de lire la taille de l'objet : " + objectName, e);
        }
    }

    /** Taille et empreinte d'un objet — le client s'en sert pour valider une reprise. */
    public record ObjectStat(long size, String etag, String contentType) {}

    public ObjectStat objectStat(String objectName) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return new ObjectStat(stat.size(), stat.etag(), stat.contentType());
        } catch (Exception e) {
            log.error("Erreur stat MinIO ({}) : {}", objectName, e.getMessage());
            throw new RuntimeException("Impossible de lire les métadonnées : " + objectName, e);
        }
    }

    /** Suppression best-effort : un objet résiduel ne doit pas faire échouer l'opération. */
    public void deleteQuietly(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            log.info("Objet MinIO supprimé : {}", objectName);
        } catch (Exception e) {
            log.warn("Suppression MinIO impossible ({}) : {}", objectName, e.getMessage());
        }
    }

    /**
     * Extrait le nom d'objet d'une URL publique produite par ce service.
     * Renvoie {@code null} si l'URL ne provient pas de ce bucket.
     */
    public String objectNameFromUrl(String url) {
        if (url == null || url.isBlank()) return null;
        String marker = "/" + bucketName + "/";
        int index = url.indexOf(marker);
        if (index < 0) return null;
        return url.substring(index + marker.length());
    }

    /** Construit un chemin d'objet unique dans un dossier donné. */
    public String buildObjectName(String folder, String filename) {
        String safeFolder = (folder != null && !folder.isBlank())
                ? folder.replaceAll("[^a-zA-Z0-9_\\-]", "_").toLowerCase()
                : "uploads";
        return safeFolder + "/" + UUID.randomUUID() + "-" + filename;
    }
}
