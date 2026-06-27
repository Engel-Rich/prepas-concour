package com.mutrix.prepa.application.usecases.admin;

import com.mutrix.prepa.application.dto.response.admin.ConcoursStatisticsResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetConcoursStatisticsUseCase {

    private final ConcoursRepository concoursRepository;
    private final ConcoursSessionRepository concoursSessionRepository;

    public ConcoursStatisticsResponse execute() {
        long totalConcours = concoursRepository.count();
        long activeConcours = concoursRepository.countByIsActive(true);
        long totalSessions = concoursSessionRepository.count();
        long activeSessions = concoursSessionRepository.countByIsActive(true);

        return ConcoursStatisticsResponse.builder()
                .totalConcours(totalConcours)
                .activeConcours(activeConcours)
                .totalSessions(totalSessions)
                .activeSessions(activeSessions)
                .build();
    }
}
