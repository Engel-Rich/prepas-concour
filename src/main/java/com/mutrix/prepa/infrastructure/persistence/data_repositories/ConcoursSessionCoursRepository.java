package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionCoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConcoursSessionCoursRepository extends JpaRepository<ConcoursSessionCoursEntity, UUID> {

    List<ConcoursSessionCoursEntity> findBySession_Id(UUID sessionId);

    List<ConcoursSessionCoursEntity> findBySession_IdAndCours_Matiere_Id(
            UUID sessionId,
            UUID matiereId);

    Optional<ConcoursSessionCoursEntity> findBySession_IdAndCours_Id(UUID sessionId, UUID coursId);

    boolean existsBySession_IdAndCours_Id(UUID sessionId, UUID coursId);

    void deleteBySession_IdAndCours_Id(UUID sessionId, UUID coursId);

    @Query("SELECT csc.session.id FROM ConcoursSessionCoursEntity csc WHERE csc.cours.id = :coursId")
    List<UUID> findSessionIdsByCours_Id(@Param("coursId") UUID coursId);
}
