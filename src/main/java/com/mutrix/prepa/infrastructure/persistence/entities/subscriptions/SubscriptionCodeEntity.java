package com.mutrix.prepa.infrastructure.persistence.entities.subscriptions;

import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import com.mutrix.prepa.infrastructure.persistence.entities.BaseEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubscriptionCodeEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeStatus status;

    /** Souscription d'achat qui a généré ce code. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionEntity subscription;

    /** Utilisateur ayant consommé le code — null tant qu'il n'est pas utilisé. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_by_user_id")
    private UserEntity usedBy;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /** Souscription créée au profit de l'activateur lors de la consommation. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activated_subscription_id")
    private SubscriptionEntity activatedSubscription;
}
