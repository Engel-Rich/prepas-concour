package com.mutrix.prepa.infrastructure.persistence.entities;

import com.mutrix.prepa.domaines.valueobjects.ConcoursSessionsStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "Concours_Sessions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConcoursSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ConcoursEntity concours;

    @Column(name = "name", nullable = false)
    private String name;

    private String description;

    private  Double amount;

    @Column(columnDefinition = "timestamp without time zone", nullable = false)
    private LocalDate startDate;

    @Column(columnDefinition = "timestamp without time zone", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ConcoursSessionsStatus status;

    @Column()
    private Boolean isActive;

    @Column()
    private LocalDateTime createdAt;

    @Column()
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;

    @PostUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PostPersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
