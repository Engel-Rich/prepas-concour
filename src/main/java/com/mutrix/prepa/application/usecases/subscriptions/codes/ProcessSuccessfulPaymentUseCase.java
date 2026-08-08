package com.mutrix.prepa.application.usecases.subscriptions.codes;

import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionCodeServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.models.NotificationModel;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.notifications.factories.NotificationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Point d'entrée unique après confirmation d'un paiement.
 *
 * <p>Deux cas :
 * <ul>
 *   <li><b>count == 1</b> — achat pour soi : la souscription passe RUNNING.</li>
 *   <li><b>count &gt; 1</b> — achat groupé : la souscription n'est <i>pas</i> activée,
 *       elle passe CODES_ISSUED et N codes d'activation partageables sont générés.
 *       L'acheteur reçoit un SMS l'invitant à les retrouver dans l'application.</li>
 * </ul>
 *
 * <p>Appelé par le webhook CamPay et par les schedulers de vérification —
 * l'opération est idempotente : une souscription déjà RUNNING ou CODES_ISSUED est ignorée.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessSuccessfulPaymentUseCase {

    private final SubscriptionServices subscriptionServices;
    private final SubscriptionCodeServices subscriptionCodeServices;
    private final ConcoursSessionServices concoursSessionServices;
    private final ConcoursServices concoursServices;
    private final UsersServices usersServices;
    private final NotificationFactory notificationFactory;

    @Transactional
    public void execute(UUID subscriptionId) {
        Subscription subscription = subscriptionServices.getById(subscriptionId.toString());

        SubscriptionStatus current = subscription.getStatus();
        if (current == SubscriptionStatus.RUNNING || current == SubscriptionStatus.CODES_ISSUED) {
            log.debug("Souscription {} déjà traitée ({}) — paiement ignoré", subscriptionId, current);
            return;
        }

        int count = subscription.getCount() != null ? subscription.getCount() : 1;

        if (count <= 1) {
            subscriptionServices.activate(subscriptionId);
            log.info("✓ Souscription {} activée (achat individuel)", subscriptionId);
            notifyBuyer(subscription, 0);
            return;
        }

        // Achat groupé → génération des codes, la souscription d'achat reste non activée
        List<SubscriptionCode> codes = subscriptionCodeServices.generateForSubscription(subscriptionId, count);
        subscriptionServices.markCodesIssued(subscriptionId);
        log.info("✓ {} code(s) d'activation générés pour la souscription {}", codes.size(), subscriptionId);

        notifyBuyer(subscription, codes.size());
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Notifie l'acheteur. Point de notification unique : le webhook comme les
     * schedulers passent par ici, et l'appel est protégé par le garde
     * d'idempotence en amont — donc exactement un envoi par souscription.
     *
     * @param codeCount 0 pour un achat individuel, N pour un achat groupé
     */
    private void notifyBuyer(Subscription subscription, int codeCount) {
        try {
            UserModel buyer = usersServices.getUserById(subscription.getUserId());
            if (buyer == null) return;

            String concoursName = resolveConcoursName(subscription.getConcoursSessionId());

            String title = "Paiement confirmé ✓";
            String body = codeCount > 0
                    ? String.format(
                        "Votre paiement de %d codes pour le concours %s a été effectué avec succès. "
                                + "Retrouvez vos codes dans l'application, rubrique « Mes codes ».",
                        codeCount, concoursName)
                    : String.format(
                        "Votre paiement pour le concours %s a été confirmé. "
                                + "Votre inscription est maintenant active.",
                        concoursName);

            if (buyer.getPhone() != null) {
                send(NotificationType.SMS, List.of(buyer.getPhone()), title, body);
            }
            if (buyer.getEmail() != null) {
                send(NotificationType.EMAIL, List.of(buyer.getEmail()), title, body);
            }
            if (buyer.getFcmToken() != null) {
                send(NotificationType.PUSH, List.of(buyer.getFcmToken()), title, body);
            }
        } catch (Exception e) {
            log.error("Erreur notification acheteur pour la souscription {}: {}",
                    subscription.getId(), e.getMessage());
        }
    }

    private String resolveConcoursName(UUID sessionId) {
        if (sessionId == null) return "sélectionné";
        try {
            ConcoursSessions session = concoursSessionServices.getConcoursSessionById(sessionId).orElse(null);
            if (session == null || session.getConcoursId() == null) return "sélectionné";
            Concours concours = concoursServices.getConcoursById(session.getConcoursId()).orElse(null);
            return concours != null && concours.getName() != null ? concours.getName() : "sélectionné";
        } catch (Exception e) {
            return "sélectionné";
        }
    }

    private void send(NotificationType type, List<String> receivers, String title, String body) {
        try {
            notificationFactory.create(type).sendNotification(
                    NotificationModel.builder()
                            .title(title)
                            .body(body)
                            .receiver(receivers)
                            .notificationType(type)
                            .build());
        } catch (Exception e) {
            log.error("Erreur envoi notification {}: {}", type, e.getMessage());
        }
    }
}
