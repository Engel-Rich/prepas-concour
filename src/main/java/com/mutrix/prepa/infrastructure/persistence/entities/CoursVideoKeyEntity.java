package com.mutrix.prepa.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clé de contenu d'une vidéo, stockée chiffrée par la clé maîtresse du serveur.
 *
 * <p>Table distincte de {@code Cours} pour que le secret ne soit pas chargé
 * à chaque lecture d'un cours.
 */
@Entity
@Table(name = "cours_video_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursVideoKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cours_id", nullable = false, unique = true)
    private UUID coursId;

    /** Identifiant inscrit dans l'en-tête du conteneur — permet la rotation. */
    @Column(name = "key_id", nullable = false)
    private UUID keyId;

    /** Base64 de {@code nonce(12) || CEK chiffrée || tag(16)}. */
    @Column(name = "wrapped_key", nullable = false, columnDefinition = "TEXT")
    private String wrappedKey;

    @Column(name = "algorithm", nullable = false, length = 64)
    private String algorithm;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
