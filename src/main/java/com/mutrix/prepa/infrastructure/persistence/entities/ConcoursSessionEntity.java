package com.mutrix.prepa.infrastructure.persistence.entities;

import com.mutrix.prepa.domaines.valueobjects.ConcoursSessionsStatus;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.UUID;

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

    @Column(name = "startdate", columnDefinition = "timestamp without time zone",nullable = false)
    private LocalDate startDate;

    @Column(name = "enddate", columnDefinition = "timestamp without time zone",nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ConcoursSessionsStatus status;

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "jsonb")
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
