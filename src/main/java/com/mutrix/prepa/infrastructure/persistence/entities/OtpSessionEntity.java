package com.mutrix.prepa.infrastructure.persistence.entities;

import com.mutrix.prepa.domaines.valueobjects.OtpType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "otpsessions")
public class OtpSessionEntity {

    @Id()
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String fullName;

    private String phone;

    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private Long expiresAt;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private OtpType otpType;

    @Column()
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
