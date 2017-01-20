package com.neat.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.neat.BR;

import java.io.Serializable;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */
public class Order extends BaseObservable implements Serializable {

    public Item item;

    public int count;

    @Bindable
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        notifyPropertyChanged(BR.count);
    }

    public void incrementCount(){
        count++;
        notifyPropertyChanged(BR.count);
    }

    public void decreaseCount(){
        count--;
        notifyPropertyChanged(BR.count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (count != order.count) return false;
        return item != null ? item.equals(order.item) : order.item == null;

    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + count;
        return result;
    }
}
