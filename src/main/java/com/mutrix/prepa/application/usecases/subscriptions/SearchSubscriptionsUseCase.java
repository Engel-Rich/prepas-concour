package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchSubscriptionsUseCase {
    private final SubscriptionServices subscriptionServices;

    public Page<SubscriptionResponse> execute(String userId, Integer page, Integer size) {
        return subscriptionServices.search(userId, page, size)
                .map(SubscriptionResponse::fromDomain);
    }

    public Page<SubscriptionResponse> execute(String userId, SubscriptionStatus status, Integer page, Integer size) {
        return subscriptionServices.search(userId, status, page, size)
                .map(SubscriptionResponse::fromDomain);
    }

    public Page<SubscriptionResponse> execute(String userId, String concoursSessionId, Integer page, Integer size) {
        return subscriptionServices.search(userId, concoursSessionId, page, size)
                .map(SubscriptionResponse::fromDomain);
    }

    public Page<SubscriptionResponse> execute(String userId, String concoursSessionId, SubscriptionStatus status, Integer page, Integer size) {
        return subscriptionServices.search(userId, concoursSessionId, status, page, size)
                .map(SubscriptionResponse::fromDomain);
    }

    public Page<SubscriptionResponse> execute(Integer page, Integer size) {
        return subscriptionServices.search(page, size)
                .map(SubscriptionResponse::fromDomain);
    }
}