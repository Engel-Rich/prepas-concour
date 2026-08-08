package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.domaines.interfaces.CoursServices;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionCoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCoursUseCase {

    private final CoursServices coursRepository;
    private final ConcoursSessionCoursRepository sessionCoursRepository;
    private final SubscriptionServices subscriptionServices;

    public CoursResponse execute(UUID id, UUID userId) {
        Cours cours = coursRepository.getCoursById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable avec l'id : " + id));

        if (Boolean.TRUE.equals(cours.getGratuit())) {
            return CoursResponse.fromDomain(cours, true);
        }

        boolean hasAccess = false;
        if (userId != null) {
            List<UUID> sessionIds = sessionCoursRepository.findSessionIdsByCours_Id(id);
            if (!sessionIds.isEmpty()) {
                hasAccess = subscriptionServices.hasActiveSubscriptionForSessions(userId, sessionIds);
            }
        }
        return CoursResponse.fromDomain(cours, hasAccess);
    }
}