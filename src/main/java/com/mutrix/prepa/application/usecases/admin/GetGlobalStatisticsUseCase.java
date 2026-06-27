package com.mutrix.prepa.application.usecases.admin;

import com.mutrix.prepa.application.dto.response.admin.GlobalStatisticsResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.MatiereRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetGlobalStatisticsUseCase {

    private final GetUserStatisticsUseCase getUserStatisticsUseCase;
    private final GetConcoursStatisticsUseCase getConcoursStatisticsUseCase;
    private final GetSubscriptionStatisticsUseCase getSubscriptionStatisticsUseCase;
    private final CoursRepository coursRepository;
    private final MatiereRepository matiereRepository;

    public GlobalStatisticsResponse execute() {
        return GlobalStatisticsResponse.builder()
                .users(getUserStatisticsUseCase.execute())
                .concours(getConcoursStatisticsUseCase.execute())
                .subscriptions(getSubscriptionStatisticsUseCase.execute())
                .totalCours(coursRepository.count())
                .totalMatieres(matiereRepository.count())
                .build();
    }
}
