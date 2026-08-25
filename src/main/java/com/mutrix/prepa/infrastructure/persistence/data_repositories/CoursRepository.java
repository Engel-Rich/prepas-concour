package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * Les filtres cumulables de la console passent par {@link JpaSpecificationExecutor} :
 * les critères non renseignés ne sont pas ajoutés à la requête, ce qui évite
 * les paramètres nuls sans type que PostgreSQL ne sait pas interpréter.
 */
public interface CoursRepository extends JpaRepository<CoursEntity, UUID>,
        JpaSpecificationExecutor<CoursEntity> {
        Page<CoursEntity> findByMatiere_Id(UUID matiereId, Pageable pageable);
        Page<CoursEntity> findByUser_Id(UUID userId, Pageable pageable);

}
