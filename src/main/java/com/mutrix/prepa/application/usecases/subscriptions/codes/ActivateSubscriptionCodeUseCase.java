package com.mutrix.prepa.application.usecases.subscriptions.codes;

import com.mutrix.prepa.application.dto.commandes.subscription.ActivateSubscriptionCodeCommand;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.implementations.subscriptions.SubscriptionCodeServiceImplement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Consomme un code d'activation au profit de l'utilisateur connecté :
 * crée une souscription RUNNING pour lui sur la session du code, puis
 * marque le code USED de façon définitive (usage unique).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivateSubscriptionCodeUseCase {

    private final SubscriptionCodeServices subscriptionCodeServices;
    private final SubscriptionServices subscriptionServices;
    private final ConcoursSessionServices concoursSessionServices;
    private final ConcoursServices concoursServices;

    @Transactional
    public SubscriptionResponse execute(ActivateSubscriptionCodeCommand command, UUID userId) {
        String normalized = SubscriptionCodeServiceImplement.normalize(command.getCode());

        SubscriptionCode code = subscriptionCodeServices.findByCode(normalized)
                .orElseThrow(() -> new EntityNotFoundException("Code d'activation invalide"));

        if (!code.isUsable()) {
            throw new IllegalStateException("Ce code a déjà été utilisé");
        }

        // Session ciblée par le code — héritée de la souscription d'achat
        Subscription purchase = subscriptionServices.getById(code.getSubscriptionId().toString());
        UUID sessionId = purchase.getConcoursSessionId();
        if (sessionId == null) {
            throw new IllegalStateException("Ce code n'est rattaché à aucune session de concours");
        }

        ConcoursSessions session = concoursSessionServices.getConcoursSessionById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session de concours introuvable : " + sessionId));

        Concours concours = session.getConcoursId() != null
                ? concoursServices.getConcoursById(session.getConcoursId()).orElse(null)
                : null;

        // Le code ne vaut que pour le concours pour lequel il a été acheté.
        // Contrôlé AVANT toute consommation : saisir un code sur la page d'un
        // autre concours doit être refusé sans le rendre inutilisable.
        UUID expectedSessionId = command.getConcoursSessionId();
        if (expectedSessionId != null && !expectedSessionId.equals(sessionId)) {
            String codeConcours = concours != null && concours.getName() != null
                    ? concours.getName()
                    : "un autre concours";
            log.info("Code {} refusé : session attendue {} ≠ session du code {}",
                    code.getCode(), expectedSessionId, sessionId);
            throw new IllegalStateException(
                    "Ce code n'est pas valable pour ce concours ");
        }

        if (!Boolean.TRUE.equals(session.getIsActive())) {
            throw new IllegalStateException("La session de concours n'est plus active");
        }

        // L'utilisateur ne peut pas cumuler deux accès pour la même session
        if (subscriptionServices.hasActiveSubscription(userId, sessionId)) {
            throw new IllegalStateException("Vous avez déjà un accès actif pour ce concours");
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("origin", "ACTIVATION_CODE");
        metadata.put("codeId", code.getId().toString());
        metadata.put("code", code.getCode());
        metadata.put("purchaseSubscriptionId", purchase.getId().toString());

        Subscription activated = subscriptionServices.save(Subscription.builder()
                .concoursSessionId(sessionId)
                .userId(userId)
                .count(1)
                .status(SubscriptionStatus.RUNNING)
                .isActive(true)
                .metadata(metadata)
                .build());

        // Consommation définitive du code — lève si un autre appel l'a devancé
        subscriptionCodeServices.consume(code.getId(), userId, activated.getId());

        log.info("✓ Code {} activé par l'utilisateur {} → souscription {}",
                code.getCode(), userId, activated.getId());

        return SubscriptionResponse.fromDomainEnriched(activated, session, concours);
    }
}
