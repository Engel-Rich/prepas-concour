package com.mutrix.prepa.domaines.models.subscriptions;

import com.mutrix.prepa.domaines.models.BaseModel;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction extends BaseModel {

    private String reference;

    private Double amount;

    private UUID subscriptionId;

    private UUID userId;

    private UUID paymentServiceId;

    private TransactionStatus status;

    private TransactionSens sens;

    private  String raisonReject;

    private  String payToken;

    private  String externalId;

    private String phoneNumber;

}
