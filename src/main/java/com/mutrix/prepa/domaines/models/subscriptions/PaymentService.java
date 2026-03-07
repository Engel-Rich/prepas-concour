package com.mutrix.prepa.domaines.models.subscriptions;

import com.mutrix.prepa.domaines.models.BaseModel;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
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
public class PaymentService extends BaseModel {

    private String logoUrl;

    private String name;

    private String description;

    private UUID paymentProviderId;

    private TransactionSens sens;

    private  String regExp;

    private  Double rate;

    private Double providerRate;

}
