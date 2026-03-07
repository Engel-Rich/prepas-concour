package com.mutrix.prepa.domaines.interfaces.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;

import java.util.List;
import java.util.UUID;

public interface PaymentServiceService {

    public PaymentService save(PaymentService service);

    public PaymentService getById(UUID id);

    public List<PaymentService> getByProviderId(UUID id);

    public List<PaymentService> getBySens(TransactionSens sens);

    public List<PaymentService> list();

    public void delete(UUID id);
}
