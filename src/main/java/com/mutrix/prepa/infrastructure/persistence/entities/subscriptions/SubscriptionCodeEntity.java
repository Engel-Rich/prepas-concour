package com.mutrix.prepa.infrastructure.persistence.entities.subscriptions;

import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import com.mutrix.prepa.infrastructure.persistence.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

    @Column(nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionEntity subscription;
}
