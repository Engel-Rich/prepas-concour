package com.mutrix.prepa.infrastructure.scheduler;

import com.mutrix.prepa.application.usecases.subscriptions.codes.ProcessSuccessfulPaymentUseCase;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.models.NotificationModel;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import com.mutrix.prepa.infrastructure.mappers.subscriptions.TransactionMapper;
import com.mutrix.prepa.infrastructure.notifications.factories.NotificationFactory;
import com.mutrix.prepa.infrastructure.payment.PaymentFactory;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.SubscriptionRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions.TransactionRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.SubscriptionEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.TransactionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionPollingScheduler {

    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UsersServices usersServices;
    private final ProcessSuccessfulPaymentUseCase processSuccessfulPaymentUseCase;
    private final NotificationFactory notificationFactory;
    private final PaymentFactory paymentFactory;
    private final TransactionMapper transactionMapper;

    @Scheduled(fixedDelay = 7000)
    @Transactional
    public void pollPendingTransactions() {
        List<TransactionEntity> pending = transactionRepository.findAllPendingWithProvider(TransactionStatus.PENDING);
        if (pending.isEmpty()) return;

        log.info("Polling {} transaction(s) en attente...", pending.size());

        for (TransactionEntity entity : pending) {
            try {
                processTransaction(entity);
            } catch (Exception e) {
                log.error("Erreur polling transaction ref={}: {}", entity.getReference(), e.getMessage());
            }
        }
    }

    private void processTransaction(TransactionEntity entity) {
        String providerName = entity.getPaymentService().getProvider().getName();
        TransactionPaymentService strategy = paymentFactory.create(providerName);

        // Vérifier le statut auprès du provider
        Transaction verified = strategy.verify(entity.getReference());
        if (verified.getStatus() == TransactionStatus.PENDING) return;

        // Mettre à jour la transaction
        entity.setStatus(verified.getStatus());
        if (verified.getExternalId() != null) entity.setExternalId(verified.getExternalId());
        if (verified.getRaisonReject() != null) entity.setRaisonReject(verified.getRaisonReject());
        transactionRepository.save(entity);

        // Mettre à jour la souscription
        SubscriptionEntity subscription = entity.getSubscription();
        if (verified.getStatus() == TransactionStatus.SUCCESS) {
            // Achat individuel → activation ; achat groupé → génération des codes.
            // La notification de succès est émise par le use case lui-même :
            // ne pas la doubler ici.
            processSuccessfulPaymentUseCase.execute(subscription.getId());
            return;
        }

        subscription.setStatus(SubscriptionStatus.PAYMENT_FAILED);
        subscriptionRepository.save(subscription);
        notifyUser(entity, verified.getStatus());
    }

    private void notifyUser(TransactionEntity entity, TransactionStatus status) {
        try {
            UserModel user = usersServices.getUserById(entity.getUser().getId());
            if (user == null) return;

            String title;
            String body;
            if (status == TransactionStatus.SUCCESS) {
                title = "Paiement confirmé ✓";
                body = String.format(
                        "Votre paiement de %.0f XAF a été confirmé. Votre inscription est maintenant active.",
                        entity.getAmount());
            } else {
                title = "Paiement échoué";
                body = "Votre paiement n'a pas abouti. Veuillez réessayer ou contacter le support.";
            }

            // SMS
            if (user.getPhone() != null) {
                sendNotification(NotificationType.SMS, List.of(user.getPhone()), title, body);
            }
            // Email
            if (user.getEmail() != null) {
                sendNotification(NotificationType.EMAIL, List.of(user.getEmail()), title, body);
            }
            // Push
            if (user.getFcmToken() != null) {
                sendNotification(NotificationType.PUSH, List.of(user.getFcmToken()), title, body);
            }
        } catch (Exception e) {
            log.error("Erreur notification utilisateur pour transaction {}: {}", entity.getReference(), e.getMessage());
        }
    }

    private void sendNotification(NotificationType type, List<String> receivers, String title, String body) {
        try {
            notificationFactory.create(type).sendNotification(
                    NotificationModel.builder()
                            .title(title)
                            .body(body)
                            .receiver(receivers)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erreur envoi notification {}: {}", type, e.getMessage());
        }
    }
}
