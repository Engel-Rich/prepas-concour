package com.mutrix.prepa.domaines.interfaces.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;

import java.util.List;
import java.util.UUID;

public interface PaymentProviderService {

    public PaymentProvider save(PaymentProvider provider);

    public PaymentProvider getByID(UUID id);

    public PaymentProvider getByName(UUID name);

    public List<PaymentProvider> list();

    public void delete(UUID id);

}
