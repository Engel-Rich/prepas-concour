package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.mappers.CoursEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionCoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetSessionCoursListUseCase {

    private final ConcoursSessionCoursRepository sessionCoursRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<CoursResponse> execute(UUID sessionId, UUID userId) {
        boolean hasAccess = userId != null &&
                subscriptionRepository.existsByUser_IdAndSessions_IdAndStatus(
                        userId, sessionId, SubscriptionStatus.RUNNING);

        return sessionCoursRepository.findBySession_Id(sessionId)
                .stream()
                .map(sc -> CoursResponse.fromDomain(
                        CoursEntityMapper.toDCoursDomain(sc.getCours()), hasAccess))
                .collect(Collectors.toList());
    }
}
