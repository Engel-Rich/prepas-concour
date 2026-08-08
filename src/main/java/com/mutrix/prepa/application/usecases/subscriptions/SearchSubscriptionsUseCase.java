package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchSubscriptionsUseCase {
    private final SubscriptionServices subscriptionServices;
    private final ConcoursSessionServices concoursSessionServices;
    private final ConcoursServices concoursServices;

    private SubscriptionResponse enrich(Subscription s) {
        ConcoursSessions session = null;
        Concours concours = null;
        if (s.getConcoursSessionId() != null) {
            session = concoursSessionServices.getConcoursSessionById(s.getConcoursSessionId()).orElse(null);
            if (session != null && session.getConcoursId() != null) {
                concours = concoursServices.getConcoursById(session.getConcoursId()).orElse(null);
            }
        }
        return SubscriptionResponse.fromDomainEnriched(s, session, concours);
    }

    public Page<SubscriptionResponse> execute(String userId, Integer page, Integer size) {
        return subscriptionServices.search(userId, page, size).map(this::enrich);
    }

    public Page<SubscriptionResponse> execute(String userId, SubscriptionStatus status, Integer page, Integer size) {
        return subscriptionServices.search(userId, status, page, size).map(this::enrich);
    }

    public Page<SubscriptionResponse> execute(String userId, String concoursSessionId, Integer page, Integer size) {
        return subscriptionServices.search(userId, concoursSessionId, page, size).map(this::enrich);
    }

    public Page<SubscriptionResponse> execute(String userId, String concoursSessionId, SubscriptionStatus status, Integer page, Integer size) {
        return subscriptionServices.search(userId, concoursSessionId, status, page, size).map(this::enrich);
    }

    public Page<SubscriptionResponse> execute(Integer page, Integer size) {
        return subscriptionServices.search(page, size).map(this::enrich);
    }
}