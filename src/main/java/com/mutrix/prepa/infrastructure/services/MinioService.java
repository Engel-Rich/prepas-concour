package com.mutrix.prepa.infrastructure.services;

import io.minio.*;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
}
