package com.neat.viewmodel;

import com.neat.dagger.SessionScope;
import com.neat.model.classes.Order;
import com.neat.model.classes.Restaurant;
import com.neat.model.classes.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by f.gatti.gomez on 06/02/2017.
 */
@SessionScope
public class SessionViewModel {

    public interface OnPendingOrdersChangedListener {
        void onPendingOrderCreated(OrderViewModel orderViewModel);
        void onPendingOrderUpdated(OrderViewModel orderViewModel);
        void onPendingOrderRemoved(OrderViewModel orderViewModel);
    }

    public interface OnOrdersPlacedListener {
        void onOrdersPlaced(List<OrderViewModel> newlyPlacedOrders);
    }

    private List<OnOrdersPlacedListener> onOrdersPlacedListeners = new LinkedList<>();
    private List<OnPendingOrdersChangedListener> onNewPendingOrderAddedListeners = new LinkedList<>();

    Session session;

    Map<Order, OrderViewModel> orderViewModelMap = new HashMap<>();

    public void newSession(Restaurant restaurant, String ownerUid, String tableId) {
        session = new Session();
        session.restaurant = restaurant;
        session.currency = restaurant.menu.currency;
    }

    /**
     * Decrease by one the count in a pending order. If the order is empty remove it.
     *
     * @param order
     * @return has the pending been removed
     */
    public boolean removeItemInPendingOrder(OrderViewModel order) {
        order.decreaseCount();
        if (order.getCount() == 0) {
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

        List<Order> newlyPlacedOrders = session.placePendingOrders();
        List<OrderViewModel> newlyPlacedOrderViewModels = new ArrayList<>();
        for (Order order : newlyPlacedOrders) {
            newlyPlacedOrderViewModels.add(orderViewModelMap.get(order));
        }

        for (OnOrdersPlacedListener l : onOrdersPlacedListeners) {
            l.onOrdersPlaced(newlyPlacedOrderViewModels);
        }

    }

    public int getPendingItemsCount() {
        return session.getPendingItemsCount();
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

    public void addOnNewPendingOrderAddedListener(OnPendingOrdersChangedListener e) {
        onNewPendingOrderAddedListeners.add(e);
    }

    public void removeOnNewPendingOrderAddedListener(OnPendingOrdersChangedListener e) {
        onNewPendingOrderAddedListeners.remove(e);
    }

    public boolean hasSessionStarted() {
        return session != null;
    }

    public OrderViewModel addPendingItem(ItemViewModel itemViewModel, int count) {

        Order order = session.addPendingItem(itemViewModel.item, count);
        OrderViewModel orderViewModel = orderViewModelMap.get(order);

        boolean isOrderNew = order.count == count;

        if (isOrderNew) {
            orderViewModel = new OrderViewModel(order);
            orderViewModelMap.put(order, orderViewModel);
        } else {
            orderViewModel.notifyChange();
        }

        for (OnPendingOrdersChangedListener listener : onNewPendingOrderAddedListeners) {
            listener.onPendingOrderCreated(orderViewModel);
        }

        return orderViewModel;
    }

    public boolean existingRequestedOrders() {
        return session.existingRequestedOrders();
    }

    public String getTotalPriceString() {
        return String.format(Locale.getDefault(), "%.2f %s", session.getTotalSum(), session.currency);
    }
}
