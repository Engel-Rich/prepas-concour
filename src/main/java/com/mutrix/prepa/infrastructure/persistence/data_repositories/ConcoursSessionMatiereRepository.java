package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionMatiereEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConcoursSessionMatiereRepository extends JpaRepository<ConcoursSessionMatiereEntity, UUID> {

    List<ConcoursSessionMatiereEntity> findBySession_Id(UUID sessionId);

    Optional<ConcoursSessionMatiereEntity> findBySession_IdAndMatiere_Id(UUID sessionId, UUID matiereId);

    boolean existsBySession_IdAndMatiere_Id(UUID sessionId, UUID matiereId);

    void deleteBySession_IdAndMatiere_Id(UUID sessionId, UUID matiereId);
}
