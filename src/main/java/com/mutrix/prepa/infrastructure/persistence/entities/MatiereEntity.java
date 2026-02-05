package com.mutrix.prepa.infrastructure.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "Matieres")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MatiereEntity{

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "matiere", cascade = CascadeType.ALL)
    @JsonIgnore
    private java.util.List<CoursEntity> cours;

    @Column(columnDefinition = "jsonb")
    private String metadata;

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
