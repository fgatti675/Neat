package com.neat.model.classes;

import java.io.Serializable;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */
public class Order implements Serializable {

    public String id;

    public Item item;

    public int count;

    public String specialInstructions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (count != order.count) return false;
        if (id != null ? !id.equals(order.id) : order.id != null) return false;
        if (item != null ? !item.equals(order.item) : order.item != null) return false;
        return specialInstructions != null ? specialInstructions.equals(order.specialInstructions) : order.specialInstructions == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + count;
        result = 31 * result + (specialInstructions != null ? specialInstructions.hashCode() : 0);
        return result;
    }
}
