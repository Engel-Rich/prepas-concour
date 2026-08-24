package com.mutrix.prepa.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "Cours")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CoursEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    private String body;

    @Column()
    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private MatiereEntity matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Boolean gratuit = false;

    /** Vrai si l'objet stocké dans le bucket est un conteneur chiffré. */
    @Column(name = "has_been_crypted", nullable = false)
    @Builder.Default
    private Boolean hasBeenCrypted = false;

    /** Dernier motif d'échec de chiffrement, exposé à l'administration. */
    @Column(name = "encryption_error", columnDefinition = "TEXT")
    private String encryptionError;

    @Column()
    private Boolean isActive;

    @Column()
    private LocalDateTime createdAt;

    @Column()
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;

}
