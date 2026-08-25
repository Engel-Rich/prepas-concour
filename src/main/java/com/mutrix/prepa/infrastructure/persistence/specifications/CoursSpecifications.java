package com.mutrix.prepa.infrastructure.persistence.specifications;

import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionCoursEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Critères de recherche des cours, combinables.
 *
 * <p>Chaque critère renvoie {@code null} lorsqu'il n'est pas renseigné :
 * Spring Data l'ignore alors purement et simplement. La requête générée ne
 * contient donc que les filtres réellement demandés.
 *
 * <p>C'est ce qui règle le problème du patron {@code (:param IS NULL OR ...)} :
 * un paramètre nul y était transmis sans type, et PostgreSQL — incapable de
 * l'inférer dans une concaténation — le prenait pour du {@code bytea}, d'où
 * l'erreur « function lower(bytea) does not exist ».
 */
public final class CoursSpecifications {

    private CoursSpecifications() {
    }

    public static Specification<CoursEntity> matiere(UUID matiereId) {
        if (matiereId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("matiere").get("id"), matiereId);
    }

    public static Specification<CoursEntity> crypted(Boolean hasBeenCrypted) {
        if (hasBeenCrypted == null) return null;
        return (root, query, cb) -> Boolean.TRUE.equals(hasBeenCrypted)
                ? cb.isTrue(root.get("hasBeenCrypted"))
                // Les cours antérieurs au chiffrement ont la colonne à NULL :
                // ils doivent apparaître parmi les vidéos « en clair ».
                : cb.or(cb.isFalse(root.get("hasBeenCrypted")),
                        cb.isNull(root.get("hasBeenCrypted")));
    }

    public static Specification<CoursEntity> titleContains(String search) {
        if (search == null || search.isBlank()) return null;
        String pattern = "%" + search.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), pattern);
    }

    /**
     * Cours rattachés à une session donnée.
     *
     * <p>Exprimé en sous-requête {@code EXISTS} plutôt qu'en jointure : une
     * jointure sur la table d'association dupliquerait les cours présents dans
     * plusieurs sessions et fausserait le comptage de la pagination.
     */
    public static Specification<CoursEntity> inSession(UUID sessionId) {
        if (sessionId == null) return null;
        return (root, query, cb) -> {
            Subquery<UUID> sub = query.subquery(UUID.class);
            Root<ConcoursSessionCoursEntity> link = sub.from(ConcoursSessionCoursEntity.class);
            sub.select(link.get("cours").get("id"))
               .where(cb.and(
                       cb.equal(link.get("session").get("id"), sessionId),
                       cb.equal(link.get("cours").get("id"), root.get("id"))));
            return cb.exists(sub);
        };
    }
}
