package com.mutrix.prepa.infrastructure.notifications.factories;

import com.mutrix.prepa.domaines.notifications.providers.WhatsAppNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.providers.whatsapp.D7NetworkNotificationProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WhatsAppNotificationFactory {

    private  final D7NetworkNotificationProvider d7NetworkNotificationProvider;
    public WhatsAppNotificationProvider create(String type) {
        switch (type) {
            case "D7_NETWORK" -> {
                return d7NetworkNotificationProvider;
            }
            default -> throw new IllegalArgumentException("Invalid WhatsApp notification provider type: " + type);
        }
    }
}
