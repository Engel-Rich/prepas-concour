package com.mutrix.prepa.application.usecases.subscriptions.codes;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeVerificationResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeVerificationResponse.Outcome;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Invalide un code et en émet un nouveau pour le même achat.
 *
 * <p>Action délibérée, réservée aux cas où la chaîne elle-même ne doit plus
 * circuler (code diffusé par erreur, litige). Elle n'est jamais déclenchée
 * automatiquement par le contrôle de cohérence : remplacer une chaîne déjà
 * partagée la rend inutilisable pour son détenteur, ce qui n'est acceptable
 * que sur décision d'un opérateur.
 *
 * <p>Le nombre total de codes exploitables reste inchangé : un invalidé,
 * un émis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueSubscriptionCodeUseCase {

    private final SubscriptionCodeServices subscriptionCodeServices;
    private final SubscriptionCodeEnricher enricher;

    @Transactional
    public SubscriptionCodeVerificationResponse execute(UUID codeId, String reason) {
        SubscriptionCode code = subscriptionCodeServices.getById(codeId);

        // Un code déjà consommé a ouvert un accès réel : le remplacer
        // reviendrait à offrir une seconde place non payée.
        if (code.getStatus() == CodeStatus.USED) {
            throw new IllegalStateException(
                    "Ce code a déjà ouvert un accès : il ne peut pas être remplacé. "
                            + "Lancez d'abord une vérification si vous pensez que l'activation est erronée.");
        }
        if (code.getSubscriptionId() == null) {
            throw new IllegalStateException("Ce code n'est rattaché à aucun achat : remplacement impossible.");
        }

        String motive = reason != null && !reason.isBlank()
                ? reason
                : "Remplacement demandé par un administrateur";

        SubscriptionCode invalidated = subscriptionCodeServices.invalidate(codeId, motive);
        SubscriptionCode replacement = subscriptionCodeServices
                .generateForSubscription(code.getSubscriptionId(), 1)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Le code de remplacement n'a pas pu être généré"));

        log.info("Code {} invalidé et remplacé par {} — {}",
                code.getCode(), replacement.getCode(), motive);

        return SubscriptionCodeVerificationResponse.builder()
                .outcome(Outcome.INVALIDATED)
                .message("Code invalidé et remplacé par « " + replacement.getCode() + " ».")
                .checks(List.of("✓ Ancien code rendu inutilisable",
                        "✓ Nouveau code émis pour le même achat"))
                .changed(true)
                .code(enricher.enrich(invalidated))
                .replacement(enricher.enrich(replacement))
                .build();
    }
}
