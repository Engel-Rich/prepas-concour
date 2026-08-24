package com.mutrix.prepa.domaines.valueobjects;

/** Plateforme de l'appareil lié à un compte. */
public enum DevicePlatform {
    ANDROID,
    IOS,
    WEB;

    /** Parse tolérant : casse libre, valeur inconnue ou nulle → null. */
    public static DevicePlatform fromHeader(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return DevicePlatform.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}