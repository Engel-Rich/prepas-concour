package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConcoursRepository extends JpaRepository<ConcoursEntity, UUID> {
    List<ConcoursEntity> findByNameContainingIgnoreCase(String name);
    long countByIsActive(Boolean isActive);
}
