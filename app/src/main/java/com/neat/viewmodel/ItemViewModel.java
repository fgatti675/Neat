package com.neat.viewmodel;

import android.content.Context;
import android.view.View;

import com.neat.model.classes.Item;

import java.io.Serializable;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */

public class ItemViewModel implements Serializable {

    Item item;

    public ItemViewModel(Context context, Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemViewModel that = (ItemViewModel) o;

        return item != null ? item.equals(that.item) : that.item == null;

    }

    @Override
    public int hashCode() {
        return item != null ? item.hashCode() : 0;
    }


    void onItemClick(View view, Item item){

    }

    void onDirectAddItemButtonClicked(View view, Item item){

    }

    public String getId() {
        return item.id;
    }

    public String getName() {
        return item.name;
    }

    public boolean isFeatured() {
        return item.featured;
    }

    public String getDescription() {
        return item.description;
    }

    public float getPrice() {
        return item.price;
    }

    public String getCurrency() {
        return item.currency;
    }

    public String getIcon() {
        return item.icon;
    }

    public String getImageUrl() {
        return item.imageUrl;
    }
}
