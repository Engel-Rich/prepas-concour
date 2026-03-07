package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoursRepository extends JpaRepository<CoursEntity, UUID> {
        Page<CoursEntity> findByMatiere_Id(UUID matiereId, Pageable pageable);
        Page<CoursEntity> findByUser_Id(UUID userId, Pageable pageable);
}
