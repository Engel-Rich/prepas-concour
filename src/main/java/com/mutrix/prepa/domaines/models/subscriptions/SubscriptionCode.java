package com.mutrix.prepa.domaines.models.subscriptions;

import com.mutrix.prepa.domaines.models.BaseModel;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;


@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscriptionCode extends BaseModel {

    private String code;

    /** Souscription d'achat (celle payée par l'acheteur) qui a généré ce code. */
    private UUID subscriptionId;

    private CodeStatus status;

    /** Utilisateur ayant consommé le code — null tant qu'il n'est pas utilisé. */
    private UUID usedByUserId;

    /** Date de consommation du code — null tant qu'il n'est pas utilisé. */
    private LocalDateTime usedAt;

    /** Souscription créée au profit de l'activateur lors de la consommation du code. */
    private UUID activatedSubscriptionId;

    /** Un code n'est consommable qu'une seule fois. */
    public boolean isUsable() {
        return status == CodeStatus.ACTIVE && usedByUserId == null;
    }
}
