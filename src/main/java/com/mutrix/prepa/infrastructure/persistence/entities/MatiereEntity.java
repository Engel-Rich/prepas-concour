package com.mutrix.prepa.infrastructure.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "Matieres")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString()
public class MatiereEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    private String description;

    @Column()
    private String logoUrl;

    @Column()
    private Boolean isActive;

    @Column()
    private LocalDateTime createdAt;

    @Column()
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matiere", cascade = CascadeType.ALL)
    @JsonIgnore
    private java.util.List<CoursEntity> cours;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;

    @PostPersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PostUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
