package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitiateSubscriptionUseCase {
    private final SubscriptionServices subscriptionServices;
    private  final TransactionServices transactionServices;
    private final ConcoursSessionServices concoursSessionServices;
    private final TransactionPaymentService transactionPaymentService;

    public SubscriptionResponse execute(Subscription subscription) {
        return SubscriptionResponse.fromDomain(subscriptionServices.save(subscription));
    }
}