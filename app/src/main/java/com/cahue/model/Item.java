package com.cahue.model;

import java.io.Serializable;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */

public class Item implements Serializable {

    public Item(String name, String description, boolean bestSelling, float price, String currency, String icon, String imageUrl) {
        this.name = name;
        this.description = description;
        this.bestSelling = bestSelling;
        this.price = price;
        this.currency = currency;
        this.icon = icon;
        this.imageUrl = imageUrl;
    }

    public String name;

    public boolean bestSelling;

    public String description;

    public float price;

    public String currency;

    public String icon;

    public String imageUrl;

}
