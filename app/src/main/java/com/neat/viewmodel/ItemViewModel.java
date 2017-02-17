package com.neat.viewmodel;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.neat.model.SessionManager;
import com.neat.model.classes.Item;
import com.neat.view.ItemDetailsActivity;

import java.io.Serializable;
import java.util.Locale;

/**
 * ViewModel representation for
 */

public class ItemViewModel extends BaseObservable implements Serializable {

    Activity activity;
    SessionManager sessionManager;
    Item item;

    public ItemViewModel(Activity activity, SessionManager sessionManager, Item item) {
        this.activity = activity;
        this.sessionManager = sessionManager;
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


    public void openItemDetails(View view) {
        ItemDetailsActivity.startItemDetailsActivity(activity, view, item);
    }

    public void addPendingItemDirectly(View view) {
        sessionManager.addPendingOrder(item, null);
    }

    @Bindable
    public String getId() {
        return item.id;
    }

    @Bindable
    public String getName() {
        return item.name;
    }

    @Bindable
    public String getDescription() {
        return item.description;
    }

    @Bindable
    public String getIcon() {
        return item.icon;
    }

    @Bindable
    public String getPriceText() {
        String formattedPrice = null;
        if (item.currency.equals("EUR"))
            formattedPrice = String.format(Locale.getDefault(), "%.2f€", item.price);
        else
            formattedPrice = String.format(Locale.getDefault(), "%.2f %s", item.price, item.currency);
        return formattedPrice;
    }
}
