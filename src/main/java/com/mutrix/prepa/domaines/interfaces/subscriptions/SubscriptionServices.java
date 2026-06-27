package com.mutrix.prepa.domaines.interfaces.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface SubscriptionServices {

    public Subscription save(Subscription subscription);

    public Subscription getById(String id);

    public Page<Subscription> search(String userid, Integer page, Integer size);

    public Page<Subscription> search(String userid, SubscriptionStatus status, Integer page, Integer size);

    public Page<Subscription> search(String userid, String concoursSessionId, Integer page, Integer size);

    public Page<Subscription> search(Integer page, Integer size);

    public Page<Subscription> search(String userid, String concoursSessionId, SubscriptionStatus status, Integer page, Integer size);

    public Page<Subscription> searchBySession(UUID sessionId, Integer page, Integer size);

    public Page<Subscription> searchByConcours(UUID concoursId, Integer page, Integer size);

    public Page<Subscription> searchByUser(UUID userId, Integer page, Integer size);

    public void delete(UUID id);
}
