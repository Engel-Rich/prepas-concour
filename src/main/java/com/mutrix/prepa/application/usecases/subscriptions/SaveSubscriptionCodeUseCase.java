package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveSubscriptionCodeUseCase {
    private final SubscriptionCodeServices subscriptionCodeServices;
    public SubscriptionCodeResponse execute(SubscriptionCode code) {
        return SubscriptionCodeResponse.fromDomain(subscriptionCodeServices.save(code));
    }
}
