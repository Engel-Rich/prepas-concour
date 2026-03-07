package com.mutrix.prepa.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "Roles")
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;
}
