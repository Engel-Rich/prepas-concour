package com.mutrix.prepa.infrastructure.persistence.entities.subscriptions;

import com.mutrix.prepa.infrastructure.persistence.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "payment_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentProviderEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String logoUrl;

    @Column(nullable = false)
    private Boolean isActive = true;
}