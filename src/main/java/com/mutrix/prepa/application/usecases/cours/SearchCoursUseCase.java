package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.infrastructure.mappers.CoursEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Recherche des cours côté administration, à filtres cumulables.
 *
 * <p>Le filtrage est fait en base plutôt que côté navigateur : la console
 * pagine, et filtrer la seule page courante donnerait des résultats faux dès
 * que le catalogue dépasse une page.
 */
@Service
@RequiredArgsConstructor
public class SearchCoursUseCase {

    private final CoursRepository coursRepository;

    @Transactional(readOnly = true)
    public PageResponse<CoursResponse> execute(
            UUID matiereId,
            UUID sessionId,
            Boolean hasBeenCrypted,
            String search,
            Integer page,
            Integer size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String normalized = (search == null || search.isBlank()) ? null : search.trim();

        return PageResponse.fromPage(
                coursRepository.search(matiereId, sessionId, hasBeenCrypted, normalized, pageable)
                        .map(CoursEntityMapper::toDCoursDomain)
                        // Vue administrateur : aucune restriction sur le videoUrl.
                        .map(cours -> CoursResponse.fromDomain(cours, true)));
    }
}
