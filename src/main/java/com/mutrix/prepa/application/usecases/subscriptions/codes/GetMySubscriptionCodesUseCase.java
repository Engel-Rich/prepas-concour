package com.mutrix.prepa.application.usecases.subscriptions.codes;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeGroupResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Codes achetés par l'utilisateur connecté, regroupés par session de concours.
 * Alimente l'écran « Mes codes » du profil mobile.
 */
@Service
@RequiredArgsConstructor
public class GetMySubscriptionCodesUseCase {

    private final SubscriptionCodeServices subscriptionCodeServices;
    private final SubscriptionCodeEnricher enricher;

    /** Liste à plat de tous mes codes, les plus récents d'abord. */
    public List<SubscriptionCodeResponse> executeFlat(UUID buyerId) {
        return subscriptionCodeServices.findAllByBuyer(buyerId)
                .stream()
                .map(enricher::enrich)
                .toList();
    }

    /** Mes codes regroupés par session de concours, avec compteurs. */
    public List<SubscriptionCodeGroupResponse> executeGrouped(UUID buyerId) {
        List<SubscriptionCode> codes = subscriptionCodeServices.findAllByBuyer(buyerId);

        // Conserve l'ordre d'arrivée (le plus récent achat en tête)
        Map<UUID, List<SubscriptionCodeResponse>> bySession = new LinkedHashMap<>();
        for (SubscriptionCode code : codes) {
            SubscriptionCodeResponse enriched = enricher.enrich(code);
            UUID key = enriched.getConcoursSessionId();
            if (key == null) continue; // code orphelin — non exploitable côté client
            bySession.computeIfAbsent(key, k -> new ArrayList<>()).add(enriched);
        }

        List<SubscriptionCodeGroupResponse> groups = new ArrayList<>(bySession.size());
        for (Map.Entry<UUID, List<SubscriptionCodeResponse>> entry : bySession.entrySet()) {
            List<SubscriptionCodeResponse> group = entry.getValue();
            group.sort(Comparator.comparing(SubscriptionCodeResponse::getCode,
                    Comparator.nullsLast(Comparator.naturalOrder())));

            SubscriptionCodeResponse head = group.get(0);
            int used = (int) group.stream().filter(c -> c.getStatus() == CodeStatus.USED).count();
            int available = (int) group.stream().filter(c -> c.getStatus() == CodeStatus.ACTIVE).count();

            groups.add(SubscriptionCodeGroupResponse.builder()
                    .concoursSessionId(entry.getKey())
                    .sessionName(head.getSessionName())
                    .sessionStartDate(head.getSessionStartDate())
                    .sessionEndDate(head.getSessionEndDate())
                    .concoursId(head.getConcoursId())
                    .concoursName(head.getConcoursName())
                    .concoursLogoUrl(head.getConcoursLogoUrl())
                    .totalCount(group.size())
                    .availableCount(available)
                    .usedCount(used)
                    .codes(group)
                    .build());
        }
        return groups;
    }
}
