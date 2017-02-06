package com.neat.model.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */

public class Session implements Serializable {

    public Restaurant restaurant;
    public String currency;

    public List<User> users = new ArrayList<>();

    public List<Order> pendingOrders = new ArrayList<>();
    public List<Order> requestedOrders = new ArrayList<>();

    public boolean paid;

    public boolean hasPendingOrders() {
        return !pendingOrders.isEmpty();
    }

    public boolean hasAnyOrder() {
        return !pendingOrders.isEmpty() || !requestedOrders.isEmpty();
    }

    public boolean existingRequestedOrders() {
        return !requestedOrders.isEmpty();
    }

    public Order addPendingItem(Item item, int count) {

        Order order = null;

        for (Order prevOrder : pendingOrders) {
            // item is already in the pending queue
            if (prevOrder.item.equals(item)) {
                order = prevOrder;
                for (int i = 0; i < count; i++) {
                    order.count++;
                }
            }
        }

        if (order == null) {
            order = new Order();
            order.item = item;
            order.count = count;

            pendingOrders.add(order);
        }

        return order;
    }

    public List<Order> placePendingOrders() {

        List<Order> newlyPlacedOrders = new ArrayList<>();

        for (Order order : pendingOrders) {
            requestedOrders.add(order);
            newlyPlacedOrders.add(order);
        }
        pendingOrders.clear();

        return newlyPlacedOrders;
    }

    public int getPendingItemsCount() {
        int count = 0;
        for (Order order : pendingOrders) {
            count += order.count;
        }
        return count;
    }

    public float getTotalSum() {
        float sum = 0F;
        for (Order order : requestedOrders) {
            sum += order.count * order.item.price;
        }
        return sum;

    }
}
