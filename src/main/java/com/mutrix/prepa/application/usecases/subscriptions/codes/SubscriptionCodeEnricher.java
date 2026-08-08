package com.mutrix.prepa.application.usecases.subscriptions.codes;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeResponse;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assemble un {@link SubscriptionCodeResponse} complet à partir d'un code :
 * remonte la souscription d'achat pour retrouver la session, le concours,
 * l'acheteur, puis l'activateur si le code a déjà été consommé.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionCodeEnricher {

    private final SubscriptionServices subscriptionServices;
    private final ConcoursSessionServices concoursSessionServices;
    private final ConcoursServices concoursServices;
    private final UsersServices usersServices;

    public SubscriptionCodeResponse enrich(SubscriptionCode code) {
        ConcoursSessions session = null;
        Concours concours = null;
        UserModel buyer = null;
        UserModel activator = null;

        try {
            if (code.getSubscriptionId() != null) {
                Subscription purchase = subscriptionServices.getById(code.getSubscriptionId().toString());
                if (purchase != null) {
                    if (purchase.getConcoursSessionId() != null) {
                        session = concoursSessionServices
                                .getConcoursSessionById(purchase.getConcoursSessionId())
                                .orElse(null);
                        if (session != null && session.getConcoursId() != null) {
                            concours = concoursServices.getConcoursById(session.getConcoursId()).orElse(null);
                        }
                    }
                    buyer = safeUser(purchase.getUserId());
                }
            }
            activator = safeUser(code.getUsedByUserId());
        } catch (Exception e) {
            log.warn("Enrichissement partiel du code {}: {}", code.getId(), e.getMessage());
        }

        return SubscriptionCodeResponse.fromDomainEnriched(code, session, concours, buyer, activator);
    }

    private UserModel safeUser(UUID userId) {
        if (userId == null) return null;
        try {
            return usersServices.getUserById(userId);
        } catch (Exception e) {
            return null;
        }
    }
}
