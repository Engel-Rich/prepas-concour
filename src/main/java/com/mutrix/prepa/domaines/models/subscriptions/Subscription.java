package com.mutrix.prepa.domaines.models.subscriptions;

import com.mutrix.prepa.domaines.models.BaseModel;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
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
public class Subscription extends BaseModel {

    private UUID concoursSessionId;

    private UUID userId;

    private  Integer count;

    private SubscriptionStatus status;

}
