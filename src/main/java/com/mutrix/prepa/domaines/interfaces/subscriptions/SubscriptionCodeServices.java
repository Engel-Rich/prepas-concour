package com.mutrix.prepa.domaines.interfaces.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.SubscriptionCode;

import java.util.UUID;

public interface SubscriptionCodeServices {

    public SubscriptionCode save(SubscriptionCode code);

    public SubscriptionCode getById(UUID id);

    public SubscriptionCode getBySubscriptionId(UUID id);

    public void delete(UUID id);
}
