package com.mutrix.prepa.domaines.valueobjects;

public enum SubscriptionStatus {
    INITIATE, PENDING, RUNNING, INACTIVE, TERMINATED, CANCELED, PAYMENT_FAILED,

    /**
     * Achat groupé (count &gt; 1) payé avec succès : la souscription n'est pas activée
     * pour l'acheteur, elle a été convertie en N codes d'activation partageables.
     */
    CODES_ISSUED,
}
