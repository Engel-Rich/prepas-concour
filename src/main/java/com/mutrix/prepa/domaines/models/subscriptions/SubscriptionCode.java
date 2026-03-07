package com.mutrix.prepa.domaines.models.subscriptions;

import com.mutrix.prepa.domaines.models.BaseModel;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
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
public class SubscriptionCode extends BaseModel {

    private String code;

    private UUID subscriptionId;

    private CodeStatus status;
}
