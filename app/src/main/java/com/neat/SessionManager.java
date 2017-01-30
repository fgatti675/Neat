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

    public interface OnNewPendingOrderAddedListener {
        void onNewPendingOrderAdded(Order order, boolean isOrderNew);
    }

    public interface OnOrdersPlacedListener {
        void onOrdersPlaced(List<Order> newlyPlacedOrders);
    }

    private Session session;

    private List<OnOrdersPlacedListener> onOrdersPlacedListeners = new LinkedList<>();
    private List<OnNewPendingOrderAddedListener> onNewPendingOrderAddedListeners = new LinkedList<>();

    public void newSession(Restaurant restaurant) {

        session = new Session();
        session.restaurant = restaurant;
        session.currency = restaurant.menu.currency;

    }

    public boolean existingRequestedOrders() {
        return !session.requestedOrders.isEmpty();
    }


    public Order addPendingItem(Item item, int count) {

        Order order = null;
        boolean isOrderNew = false;

        for (Order prevOrder : session.pendingOrders) {
            // item is already in the pending queue
            if (prevOrder.item.equals(item)) {
                order = prevOrder;
                for (int i = 0; i < count; i++) {
                    order.incrementCount();
                }
            }
        }

        if (order == null) {
            order = new Order();
            order.item = item;
            order.count = count;
            session.pendingOrders.add(order);
            isOrderNew = true;
        }

        for (OnNewPendingOrderAddedListener listener : onNewPendingOrderAddedListeners) {
            listener.onNewPendingOrderAdded(order, isOrderNew);
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
        return sum;
    }

    public int getPendingItemsCount(){
        int i = 0;
        for (Order order : session.pendingOrders) {
            i += order.getCount();
        }
        return i;
    }

    public List<Order> getPendingOrders(){
        return session.pendingOrders;
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

    public void addOnNewPendingOrderAddedListener(OnNewPendingOrderAddedListener e) {
        onNewPendingOrderAddedListeners.add(e);
    }

    public void removeOnNewPendingOrderAddedListener(OnNewPendingOrderAddedListener e) {
        onNewPendingOrderAddedListeners.remove(e);
    }

    public boolean hasSessionStarted() {
        return session != null;
    }

}
