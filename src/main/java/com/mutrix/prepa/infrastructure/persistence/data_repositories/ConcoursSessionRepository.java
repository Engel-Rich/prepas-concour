package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.domaines.valueobjects.ConcoursSessionsStatus;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConcoursSessionRepository extends JpaRepository<ConcoursSessionEntity, UUID> {
    Page<ConcoursSessionEntity> findByConcours_Id(UUID concoursId, Pageable pageable);
    long countByIsActive(Boolean isActive);
    Optional<ConcoursSessionEntity> findFirstByConcours_IdAndStatus(UUID concoursId, ConcoursSessionsStatus status);
}
