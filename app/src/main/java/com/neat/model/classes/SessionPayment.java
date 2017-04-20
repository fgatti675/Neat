package com.neat.model.classes;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */
public class SessionPayment implements Serializable {

    public String id;
    public Restaurant restaurant;
    public Date creationDate;
    public Table table;

    public String currency;

    public Map<String, User> users = new HashMap<>();

    public Set<Order> pendingOrders = new LinkedHashSet<>();
    public Set<Order> requestedOrders = new LinkedHashSet<>();

    public boolean active;
    public boolean paid;

    public boolean hasPendingOrders() {
        return !pendingOrders.isEmpty();
    }

    public boolean hasAnyOrder() {
        return !pendingOrders.isEmpty() || !requestedOrders.isEmpty();
    }

    public boolean hasRequestedOrders() {
        return !requestedOrders.isEmpty();
    }

    public int getPendingItemsCount() {
        return pendingOrders.size();
    }

    public float getTotalSum() {
        float sum = 0F;
        for (Order order : requestedOrders) {
            sum += order.item.price;
        }
        return sum;
    }

}
