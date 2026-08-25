package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CoursRepository extends JpaRepository<CoursEntity, UUID> {
        Page<CoursEntity> findByMatiere_Id(UUID matiereId, Pageable pageable);
        Page<CoursEntity> findByUser_Id(UUID userId, Pageable pageable);

        /**
         * Recherche administrateur à filtres cumulables.
         *
         * <p>Chaque critère est ignoré lorsqu'il vaut {@code null} : la même
         * requête sert donc pour aucun filtre, un seul, ou toute combinaison —
         * sans multiplier les méthodes de repository.
         *
         * <p>Le filtre par session passe par la table d'association ; le
         * {@code DISTINCT} évite les doublons lorsqu'un cours est rattaché à
         * plusieurs sessions.
         */
        @Query(value = """
                SELECT DISTINCT c FROM CoursEntity c
                LEFT JOIN ConcoursSessionCoursEntity csc ON csc.cours.id = c.id
                WHERE (:matiereId IS NULL OR c.matiere.id = :matiereId)
                  AND (:sessionId IS NULL OR csc.session.id = :sessionId)
                  AND (:hasBeenCrypted IS NULL OR c.hasBeenCrypted = :hasBeenCrypted)
                  AND (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')))
                """,
                countQuery = """
                SELECT COUNT(DISTINCT c) FROM CoursEntity c
                LEFT JOIN ConcoursSessionCoursEntity csc ON csc.cours.id = c.id
                WHERE (:matiereId IS NULL OR c.matiere.id = :matiereId)
                  AND (:sessionId IS NULL OR csc.session.id = :sessionId)
                  AND (:hasBeenCrypted IS NULL OR c.hasBeenCrypted = :hasBeenCrypted)
                  AND (:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')))
                """)
        Page<CoursEntity> search(
                @Param("matiereId") UUID matiereId,
                @Param("sessionId") UUID sessionId,
                @Param("hasBeenCrypted") Boolean hasBeenCrypted,
                @Param("search") String search,
                Pageable pageable);
}
