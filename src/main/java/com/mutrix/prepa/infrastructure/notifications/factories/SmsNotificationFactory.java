package com.mutrix.prepa.infrastructure.notifications.factories;

import com.mutrix.prepa.domaines.notifications.providers.SmsNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.providers.sms.NehxaNotificationProvider;
import com.mutrix.prepa.infrastructure.notifications.providers.sms.TwillioSMSNotificationProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SmsNotificationFactory {
    private  final TwillioSMSNotificationProvider twillioSMSNotificationProvider;
    private  final NehxaNotificationProvider nehxaNotificationProvider;

    public SmsNotificationProvider create(String type) {
        return switch (type) {
            case "TWILLIO" -> twillioSMSNotificationProvider;
            case "NEHXA" -> nehxaNotificationProvider;
            default -> throw new IllegalArgumentException("Invalid SMS notification provider type: " + type);
        };
    }
}
