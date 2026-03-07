package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RolesRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> getByName(String name);
}
