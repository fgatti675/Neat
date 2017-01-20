package com.neat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */

public class Session implements Serializable {

    public Restaurant restaurant;
    public String currency;

    public List<Order> pendingOrders = new ArrayList<>();
    public List<Order> requestedOrders = new ArrayList<>();
    public List<Order> deliveredOrders = new ArrayList<>();

    public boolean paid;

    public boolean hasPendingOrders() {
        return !pendingOrders.isEmpty();
    }

    public boolean hasAnyOrder() {
        return !pendingOrders.isEmpty() || !requestedOrders.isEmpty() || !deliveredOrders.isEmpty();
    }

}
