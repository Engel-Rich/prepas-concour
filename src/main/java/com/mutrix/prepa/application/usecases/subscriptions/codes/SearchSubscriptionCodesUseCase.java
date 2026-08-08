package com.mutrix.prepa.application.usecases.subscriptions.codes;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/** Recherche des codes de souscription côté administration. */
@Service
@RequiredArgsConstructor
public class SearchSubscriptionCodesUseCase {

    private final SubscriptionCodeServices subscriptionCodeServices;
    private final SubscriptionCodeEnricher enricher;

    public Page<SubscriptionCodeResponse> execute(Integer page, Integer size) {
        return subscriptionCodeServices.search(page, size).map(enricher::enrich);
    }

    public Page<SubscriptionCodeResponse> execute(CodeStatus status, Integer page, Integer size) {
        return subscriptionCodeServices.search(status, page, size).map(enricher::enrich);
    }

    public Page<SubscriptionCodeResponse> executeBySession(UUID sessionId, Integer page, Integer size) {
        return subscriptionCodeServices.searchBySession(sessionId, page, size).map(enricher::enrich);
    }

    public Page<SubscriptionCodeResponse> executeByConcours(UUID concoursId, Integer page, Integer size) {
        return subscriptionCodeServices.searchByConcours(concoursId, page, size).map(enricher::enrich);
    }

    public Page<SubscriptionCodeResponse> executeByBuyer(UUID buyerId, Integer page, Integer size) {
        return subscriptionCodeServices.searchByBuyer(buyerId, page, size).map(enricher::enrich);
    }

    public SubscriptionCodeResponse executeById(UUID id) {
        return enricher.enrich(subscriptionCodeServices.getById(id));
    }

    /** Tous les codes issus d'une souscription d'achat donnée. */
    public List<SubscriptionCodeResponse> executeBySubscription(UUID subscriptionId) {
        return subscriptionCodeServices.findAllBySubscriptionId(subscriptionId)
                .stream().map(enricher::enrich).toList();
    }
}
