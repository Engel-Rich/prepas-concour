package com.mutrix.prepa.infrastructure.persistence.entities.subscriptions;

import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.infrastructure.persistence.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "payment_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentServiceEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    private String logoUrl;

    private String regExp;

    private Double rate;

    private Double providerRate;

    @Enumerated(EnumType.STRING)
    private TransactionSens sens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private PaymentProviderEntity provider;
}
