package com.neat.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.neat.BR;
import com.neat.model.SessionManager;
import com.neat.model.classes.Item;
import com.neat.model.classes.Order;

import java.io.Serializable;
import java.util.Locale;
import java.util.Stack;

/**
 * Map a list of orders of the same item to a single {@link PendingOrderViewModel}
 */
public class PendingOrderViewModel extends BaseObservable implements Serializable {

    private SessionManager sessionManager;
    private Item item;
    private Stack<Order> orders;

    public PendingOrderViewModel(SessionManager sessionManager, Item item) {
        this.sessionManager = sessionManager;
        this.item = item;
        this.orders = new Stack<>();
    }

    public void addOrder(Order order) {
        orders.add(order);
        notifyPropertyChanged(BR.count);
        notifyPropertyChanged(BR.formattedPrice);
    }

    /**
     *
     * @param order
     * @return Should be removed
     */
    public boolean removeOrder(Order order) {
        orders.remove(order);
        notifyPropertyChanged(BR.count);
        return orders.isEmpty();
    }

    public void onOrderClicked(View view) {

    }

    public String getId() {
        return item.id;
    } // TODO

    public void onOrderClick(View view) {

    }

    public void onOrderRemovedClick(View view) {
        Order order = orders.pop();
        sessionManager.removePendingOrder(order);
        notifyPropertyChanged(BR.count);
    }

    @Bindable
    public String getIcon() {
        return item.icon;
    }

    @Bindable
    public int getCount() {
        return orders.size();
    }

    @Bindable
    public String getName() {
        return item.name;
    }

    @Bindable
    public String getFormattedPrice() {
        String formattedPrice = null;
        float totalPrice = 0;
        for (Order order : orders) totalPrice += order.getPrice();
        if (item.currency.equals("EUR")) {
            formattedPrice = String.format(Locale.getDefault(), "%.2f€", totalPrice);
        } else {
            formattedPrice = String.format(Locale.getDefault(), "%.2f %s", totalPrice, item.currency);
        }
        return formattedPrice;
    }
}
