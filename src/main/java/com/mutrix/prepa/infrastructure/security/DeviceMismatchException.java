package com.mutrix.prepa.infrastructure.security;

/**
 * L'appareil émetteur ne correspond pas à l'appareil lié au compte.
 *
 * <p>Porte un code métier stable que le client mobile détecte pour déclencher
 * une déconnexion complète.
 */
public class DeviceMismatchException extends RuntimeException {

    public static final String CODE = "DEVICE_MISMATCH";

    public DeviceMismatchException(String message) {
        super(message);
    }
}