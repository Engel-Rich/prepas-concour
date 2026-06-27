package com.mutrix.prepa.application.usecases.admin;

import com.mutrix.prepa.application.dto.response.admin.SubscriptionStatisticsResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@UseCase
@RequiredArgsConstructor
public class GetSubscriptionStatisticsUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionStatisticsResponse execute() {
        long total = subscriptionRepository.count();
        long active = subscriptionRepository.countByStatus(SubscriptionStatus.RUNNING);
        long pending = subscriptionRepository.countByStatus(SubscriptionStatus.INITIATE);
        long cancelled = subscriptionRepository.countByStatus(SubscriptionStatus.CANCELED);
        long expired = subscriptionRepository.countByStatus(SubscriptionStatus.TERMINATED);
        long newLast30Days = subscriptionRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(30));

        return SubscriptionStatisticsResponse.builder()
                .total(total)
                .active(active)
                .pending(pending)
                .cancelled(cancelled)
                .expired(expired)
                .newLast30Days(newLast30Days)
                .build();
    }
}
