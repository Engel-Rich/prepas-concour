package com.mutrix.prepa.domaines.interfaces;

import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.valueobjects.DevicePlatform;

import java.util.UUID;

/**
 * Liaison entre un compte et son appareil actif.
 *
 * <p>Règle retenue : <b>dernier arrivé gagne</b>. Une authentification complète
 * réussie réécrit l'appareil actif ; l'ancien appareil se voit refuser ses
 * requêtes suivantes et doit se reconnecter.
 */
public interface DeviceBindingService {

    /**
     * Lie l'appareil au compte à l'issue d'une authentification réussie.
     * Écrase toute liaison précédente et révoque les sessions Firebase de
     * l'ancien appareil.
     */
    void bind(UUID userId, String deviceId, DevicePlatform platform);

    /**
     * Contrôle qu'une requête authentifiée provient bien de l'appareil lié.
     *
     * <p>Si le compte n'a pas encore d'appareil (comptes antérieurs à la
     * fonctionnalité), l'appareil courant est adopté et la requête passe.
     *
     * @throws com.mutrix.prepa.infrastructure.security.DeviceMismatchException
     *         si l'appareil ne correspond pas à celui enregistré
     */
    void verify(UserModel user, String deviceId, DevicePlatform platform);
}