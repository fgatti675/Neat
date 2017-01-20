package com.neat.model;

import java.io.Serializable;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */

public class Item implements Serializable {

    public Item() {
    }

    public Item(String id, String name, String description, boolean featured, float price, String currency, String icon, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.featured = featured;
        this.price = price;
        this.currency = currency;
        this.icon = icon;
        this.imageUrl = imageUrl;
    }

    public String id;

    public String name;

    public boolean featured;

    public String description;

    public float price;

    public String currency;

    public String icon;

    public String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return id != null ? id.equals(item.id) : item.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
