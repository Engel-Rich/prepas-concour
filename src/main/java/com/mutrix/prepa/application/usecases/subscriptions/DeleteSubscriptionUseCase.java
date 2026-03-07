package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteSubscriptionUseCase {
    private final SubscriptionServices subscriptionServices;
    public void execute(UUID id) { subscriptionServices.delete(id); }
}