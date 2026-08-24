package com.mutrix.prepa.infrastructure.implementations;

import com.mutrix.prepa.domaines.interfaces.DeviceBindingService;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.services.FirebaseService;
import com.mutrix.prepa.domaines.valueobjects.DevicePlatform;
import com.mutrix.prepa.infrastructure.security.DeviceMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceBindingServiceImplement implements DeviceBindingService {

    private final UsersServices usersServices;
    private final FirebaseService firebaseService;

    @Override
    public void bind(UUID userId, String deviceId, DevicePlatform platform) {
        if (deviceId == null || deviceId.isBlank()) {
            log.warn("[Device] Liaison ignorée pour {} : aucun deviceId fourni", userId);
            return;
        }

        UserModel user = usersServices.getUserById(userId);
        if (user == null) return;

        String previous = user.getDeviceId();
        boolean isNewDevice = previous != null && !previous.isBlank()
                && !previous.equals(deviceId);

        // « Dernier arrivé gagne » : l'ancien appareil doit perdre l'accès
        // immédiatement. Un simple contrôle applicatif ne suffirait pas —
        // son ID Token resterait valide jusqu'à une heure.
        if (isNewDevice) {
            try {
                firebaseService.revokeRefreshTokens(user.getFirebaseUid());
                log.info("[Device] Compte {} : appareil remplacé ({} -> {}), sessions révoquées",
                        userId, previous, deviceId);
            } catch (Exception e) {
                // La liaison doit aboutir même si la révocation échoue :
                // le filtre refusera de toute façon l'ancien appareil.
                log.error("[Device] Révocation Firebase impossible pour {} : {}",
                        user.getFirebaseUid(), e.getMessage());
            }
        }

        user.setDeviceId(deviceId);
        user.setPlatform(platform);
        usersServices.updateUser(user, null);
    }

    @Override
    public void verify(UserModel user, String deviceId, DevicePlatform platform) {
        String registered = user.getDeviceId();

        // Compte antérieur à la fonctionnalité : adoption silencieuse.
        if (registered == null || registered.isBlank()) {
            user.setDeviceId(deviceId);
            user.setPlatform(platform);
            usersServices.updateUser(user, null);
            log.info("[Device] Compte {} lié à son premier appareil {}", user.getId(), deviceId);
            return;
        }

        if (!registered.equals(deviceId)) {
            throw new DeviceMismatchException(
                    "Votre compte est actuellement utilisé sur un autre appareil. "
                            + "Reconnectez-vous pour continuer sur celui-ci.");
        }

        // La plateforme n'invalide pas la session à elle seule : un même
        // identifiant ne peut pas changer de système. On la corrige si elle
        // manque (comptes adoptés avant l'envoi de l'en-tête).
        if (platform != null && user.getPlatform() != platform) {
            user.setPlatform(platform);
            usersServices.updateUser(user, null);
        }
    }
}