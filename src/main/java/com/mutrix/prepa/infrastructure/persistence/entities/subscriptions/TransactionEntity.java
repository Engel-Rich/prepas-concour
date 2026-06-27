package com.mutrix.prepa.infrastructure.persistence.entities.subscriptions;

import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import com.mutrix.prepa.infrastructure.persistence.entities.BaseEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TransactionEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionSens sens;

    private String payToken;

    private String externalId;

    private String raisonReject;

    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionEntity subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_service_id", nullable = false)
    private PaymentServiceEntity paymentService;
}
