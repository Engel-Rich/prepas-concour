package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSubscriptionByIdUseCase {
    private final SubscriptionServices subscriptionServices;

    public SubscriptionResponse execute(String id) {
        return SubscriptionResponse.fromDomain(subscriptionServices.getById(id));
    }
}