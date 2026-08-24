package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.CoursVideoKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CoursVideoKeyRepository extends JpaRepository<CoursVideoKeyEntity, UUID> {

    Optional<CoursVideoKeyEntity> findByCoursId(UUID coursId);

    void deleteByCoursId(UUID coursId);
}
