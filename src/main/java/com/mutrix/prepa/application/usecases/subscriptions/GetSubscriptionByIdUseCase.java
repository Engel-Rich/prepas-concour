package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetSubscriptionByIdUseCase {
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

    /** Accès admin — sans vérification d'ownership. */
    public SubscriptionResponse execute(String id) {
        return enrich(subscriptionServices.getById(id));
    }

    /** Accès utilisateur — vérifie l'ownership avant de retourner. */
    public SubscriptionResponse execute(String id, UUID userId) {
        return enrich(subscriptionServices.getByIdAndUser(id, userId));
    }
}