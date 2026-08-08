package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteSubscriptionUseCase {
    private final SubscriptionServices subscriptionServices;

    /** Accès admin — suppression sans vérification d'ownership. */
    public void execute(UUID id) { subscriptionServices.delete(id); }

    /** Accès utilisateur — vérifie ownership et interdit la suppression d'une souscription RUNNING. */
    public void executeByUser(UUID id, UUID userId) { subscriptionServices.deleteByIdAndUser(id, userId); }
}