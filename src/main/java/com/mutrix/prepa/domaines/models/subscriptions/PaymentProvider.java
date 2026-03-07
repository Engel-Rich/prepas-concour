package com.mutrix.prepa.domaines.models.subscriptions;

import com.mutrix.prepa.domaines.models.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentProvider extends BaseModel {

    private  String name;

    private  String description;

    private String logoUrl;
}
