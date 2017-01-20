package com.neat;

import com.neat.model.Item;
import com.neat.model.Order;
import com.neat.model.Restaurant;
import com.neat.model.Session;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by f.gatti.gomez on 19/01/2017.
 */

public class SessionManager {

    public interface OnOrdersPlacedListener{
        void onOrdersPlaced(List<Order> newlyPlacedOrders);
    }

    public interface OnOrdersDeliveredListener{
        void onOrdersDelivered(List<Order> newlyPlacedOrders);
    }

    private Session session;

    private List<OnOrdersPlacedListener> onOrdersPlacedListeners = new LinkedList<>();

    public void newSession(Restaurant restaurant) {

        session = new Session();
        session.restaurant = restaurant;
        session.currency = restaurant.menu.currency;

    }

    public boolean existingRequestedOrders() {
        return !session.requestedOrders.isEmpty();
    }


    public Order requestItem(Item item) {

        Order order = null;

        for (Order prevOrder : session.pendingOrders) {
            // item is already in the pending queue
            if (prevOrder.item.equals(item)) {
                order = prevOrder;
                order.incrementCount();
            }
        }

        if (order == null) {
            order = new Order();
            order.item = item;
            order.count = 1;
            session.pendingOrders.add(order);
        }

        return order;
    }

    /**
     * Decrese by one the count in a pending order. If the order is empty remove it.
     *
     * @param order
     * @return has the pending been removed
     */
    public boolean removeItemInPendingOrder(Order order) {
        order.decreaseCount();
        if (order.count == 0) {
            session.pendingOrders.remove(order);
            return true;
        }
        return false;
    }

    public boolean hasPendingOrders() {
        if (session == null) throw new RuntimeException("Session not created");
        return session.hasPendingOrders();
    }

    public boolean hasAnyOrder() {
        if (session == null) throw new RuntimeException("Session not created");
        return session.hasAnyOrder();
    }

    public void placePendingOrders() {

        List<Order> newlyPlacedOrders = new ArrayList<>();

        for (Order order : session.pendingOrders) {
            session.requestedOrders.add(order);
            newlyPlacedOrders.add(order);
        }
        session.pendingOrders.clear();

        for (OnOrdersPlacedListener l : onOrdersPlacedListeners) {
            l.onOrdersPlaced(newlyPlacedOrders);
        }

    }

    public float getRequestedItemsPrice() {

        float sum = 0F;

        for (Order order : session.requestedOrders) {
            sum += order.getCount() * order.item.price;
        }
        for (Order order : session.deliveredOrders) {
            sum += order.getCount() * order.item.price;
        }
        return sum;
    }

    public String getCurrency() {
        return session.currency;
    }

    public void addOnOrdersPlacedListener(OnOrdersPlacedListener e) {
        onOrdersPlacedListeners.add(e);
    }

    public void removeOnOrdersPlacedListener(OnOrdersPlacedListener e) {
        onOrdersPlacedListeners.remove(e);
    }

}
