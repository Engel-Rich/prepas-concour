package com.mutrix.prepa.infrastructure.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Concours")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ConcoursEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "logourl")
    private String logoUrl;

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @Column(name = "updatedat")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "concours", cascade = CascadeType.ALL)
    @JsonIgnore
    private java.util.List<ConcoursSessionEntity> sessions;

    @PostPersist
    public  void  onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PostUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
