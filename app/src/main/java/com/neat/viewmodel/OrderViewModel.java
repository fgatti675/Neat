package com.neat.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.neat.BR;
import com.neat.model.classes.Order;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */
public class OrderViewModel extends BaseObservable implements Serializable {

    public Order order;

    public OrderViewModel(Order order) {
        this.order = order;
    }

    public void setCount(int count) {
        this.order.count = count;
        notifyPropertyChanged(BR.count);
    }

    public void decreaseCount(){
        order.count--;
        notifyPropertyChanged(BR.count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderViewModel that = (OrderViewModel) o;

        return order != null ? order.equals(that.order) : that.order == null;

    }

    @Override
    public int hashCode() {
        return order != null ? order.hashCode() : 0;
    }

    public void onOrderClicked(View view) {

    }

    @Bindable
    public String getSpecialInstructions() {
        return order.specialInstructions;
    }

    @Bindable
    public int getCount() {
        return order.count;
    }

    @Bindable
    public String getFormattedPrice(){
        String formattedPrice = null;
        float totalPrice = getCount() * order.item.price;
        if (order.item.currency.equals("EUR")) {
            formattedPrice = String.format(Locale.getDefault(), "%.2f€", totalPrice);
        } else {
            formattedPrice = String.format(Locale.getDefault(), "%.2f %s", totalPrice, order.item.currency);
        }
        return formattedPrice;
    }

    public String getId(){
        return order.id;
    }

    public void onOrderClick(View view){

    }

    public void onOrderRemovedClick(View view){
        decreaseOrderCount(order, expandedBinding.getRoot(), collapsedBinding.getRoot());
    }

    public String getIconId(){
        return order.item.icon;
    }
}
