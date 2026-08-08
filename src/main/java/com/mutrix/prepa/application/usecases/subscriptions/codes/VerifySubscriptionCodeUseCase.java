package com.mutrix.prepa.application.usecases.subscriptions.codes;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeVerificationResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeVerificationResponse.Outcome;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Contrôle de cohérence d'un code d'activation, avec réparation.
 *
 * <p>Trois conditions doivent être réunies pour qu'un code soit exploitable :
 * <ol>
 *   <li>l'achat qui l'a produit est bien payé (souscription CODES_ISSUED) ;</li>
 *   <li>la session de concours visée existe et est encore active ;</li>
 *   <li>s'il est marqué USED, l'activation correspondante existe réellement.</li>
 * </ol>
 *
 * <p>La réparation est volontairement conservatrice : un code consommé sans
 * activation réelle est <b>remis à disposition avec la même chaîne</b> plutôt
 * qu'invalidé et remplacé — l'acheteur a pu la transmettre à quelqu'un, et lui
 * substituer un nouveau code rendrait celle-ci inutilisable pour son détenteur.
 * Le remplacement reste possible via une action distincte et explicite.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifySubscriptionCodeUseCase {

    private final SubscriptionCodeServices subscriptionCodeServices;
    private final SubscriptionServices subscriptionServices;
    private final ConcoursSessionServices concoursSessionServices;
    private final SubscriptionCodeEnricher enricher;

    @Transactional
    public SubscriptionCodeVerificationResponse execute(UUID codeId) {
        SubscriptionCode code = subscriptionCodeServices.getById(codeId);
        List<String> checks = new ArrayList<>();

        // ── 1. L'achat à l'origine du code ────────────────────────────────────
        Subscription purchase = null;
        try {
            if (code.getSubscriptionId() != null) {
                purchase = subscriptionServices.getById(code.getSubscriptionId().toString());
            }
        } catch (Exception e) {
            purchase = null;
        }

        if (purchase == null) {
            checks.add("✗ Souscription d'achat introuvable");
            return repair(code, checks, Outcome.INVALIDATED,
                    "Le code n'est rattaché à aucun achat identifiable.",
                    "Souscription d'achat introuvable");
        }
        if (purchase.getStatus() != SubscriptionStatus.CODES_ISSUED) {
            checks.add("✗ Achat non honoré (statut " + purchase.getStatus() + ")");
            return repair(code, checks, Outcome.INVALIDATED,
                    "L'achat à l'origine de ce code n'a pas été honoré ("
                            + purchase.getStatus() + ") : le code ne devrait pas être utilisable.",
                    "Achat au statut " + purchase.getStatus());
        }
        checks.add("✓ Achat honoré (souscription " + purchase.getStatus() + ")");

        // ── 2. La session visée ───────────────────────────────────────────────
        ConcoursSessions session = purchase.getConcoursSessionId() != null
                ? concoursSessionServices.getConcoursSessionById(purchase.getConcoursSessionId()).orElse(null)
                : null;

        if (session == null) {
            checks.add("✗ Session de concours introuvable");
            return repair(code, checks, Outcome.INVALIDATED,
                    "La session de concours visée par ce code n'existe plus.",
                    "Session introuvable");
        }
        if (!Boolean.TRUE.equals(session.getIsActive())) {
            checks.add("✗ Session close");
            return repair(code, checks, Outcome.EXPIRED,
                    "La session « " + session.getName() + " » n'est plus active : le code est expiré.",
                    "Session close");
        }
        checks.add("✓ Session active — " + session.getName());

        // ── 3. Cohérence de la consommation ───────────────────────────────────
        if (code.getStatus() == CodeStatus.USED) {
            Subscription activated = null;
            if (code.getActivatedSubscriptionId() != null) {
                try {
                    activated = subscriptionServices
                            .getById(code.getActivatedSubscriptionId().toString());
                } catch (Exception e) {
                    activated = null;
                }
            }

            boolean traceComplete = code.getUsedByUserId() != null
                    && code.getUsedAt() != null
                    && activated != null;

            // L'accès promis doit exister et porter sur la même session
            boolean accessGranted = traceComplete
                    && activated.getStatus() == SubscriptionStatus.RUNNING
                    && session.getId().equals(activated.getConcoursSessionId());

            if (!accessGranted) {
                checks.add("✗ Marqué utilisé sans accès correspondant");
                return repair(code, checks, Outcome.RELEASED,
                        "Ce code était marqué utilisé alors qu'aucun accès valide n'a été ouvert : "
                                + "il a été remis à disposition et reste utilisable tel quel.",
                        "Consommation sans activation correspondante");
            }

            checks.add("✓ Activation cohérente — accès ouvert le " + code.getUsedAt());
            return done(code, checks, Outcome.USED_CONSISTENT,
                    "Code correctement consommé : l'accès correspondant est bien actif.", false, null);
        }

        // ── Code encore disponible ────────────────────────────────────────────
        if (code.getStatus() != CodeStatus.ACTIVE) {
            checks.add("✗ Statut " + code.getStatus() + " sans motif détecté");
            return repair(code, checks, Outcome.RELEASED,
                    "Aucune anomalie détectée : le code a été remis à disposition.",
                    "Statut " + code.getStatus() + " sans cause identifiée");
        }

        checks.add("✓ Code disponible");
        return done(code, checks, Outcome.VALID,
                "Toutes les conditions sont réunies : ce code est utilisable.", false, null);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private SubscriptionCodeVerificationResponse repair(
            SubscriptionCode code, List<String> checks, Outcome outcome,
            String message, String reason) {

        // Rien à faire si le code porte déjà l'état visé
        CodeStatus target = switch (outcome) {
            case RELEASED -> CodeStatus.ACTIVE;
            case INVALIDATED -> CodeStatus.INVALID;
            case EXPIRED -> CodeStatus.EXPIRED;
            default -> code.getStatus();
        };
        if (code.getStatus() == target && outcome != Outcome.RELEASED) {
            return done(code, checks, outcome, message, false, null);
        }

        SubscriptionCode updated = switch (outcome) {
            case RELEASED -> subscriptionCodeServices.release(code.getId(), reason);
            case INVALIDATED -> subscriptionCodeServices.invalidate(code.getId(), reason);
            case EXPIRED -> subscriptionCodeServices.markExpired(code.getId(), reason);
            default -> code;
        };

        log.info("Contrôle du code {} → {} ({})", code.getCode(), outcome, reason);
        return done(updated, checks, outcome, message, true, null);
    }

    private SubscriptionCodeVerificationResponse done(
            SubscriptionCode code, List<String> checks, Outcome outcome,
            String message, boolean changed, SubscriptionCode replacement) {

        return SubscriptionCodeVerificationResponse.builder()
                .outcome(outcome)
                .message(message)
                .checks(checks)
                .changed(changed)
                .code(enricher.enrich(code))
                .replacement(replacement != null ? enricher.enrich(replacement) : null)
                .build();
    }
}
