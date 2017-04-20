package com.neat.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableInt;
import android.view.View;

import com.neat.model.SessionManager;
import com.neat.model.classes.Item;
import com.neat.view.ItemDetailsActivity;

import java.io.Serializable;
import java.util.Locale;

import static com.neat.view.ItemDetailsActivity.EXTRA_ITEM;
import static com.neat.view.ItemDetailsActivity.EXTRA_ITEM_COUNT;
import static com.neat.view.ItemDetailsActivity.RESULT_ITEM_ADDED;

/**
 * ViewModel representation for
 */

public class ItemDetailsViewModel implements Serializable {

    Activity activity;
    SessionManager sessionManager;
    Item item;
    ObservableInt orderCount = new ObservableInt(1);
    String additionalInstructions;

    public ItemDetailsViewModel(Activity activity, SessionManager sessionManager, Item item) {
        this.activity = activity;
        this.sessionManager = sessionManager;
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemDetailsViewModel that = (ItemDetailsViewModel) o;

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

    public String getId() {
        return item.id;
    }

    public String getName() {
        return item.name;
    }

    public String getDescription() {
        return item.description;
    }

    public String getIcon() {
        return item.icon;
    }

    public String getPriceText() {
        String formattedPrice = null;
        if (item.currency.equals("EUR"))
            formattedPrice = String.format(Locale.getDefault(), "%.2f€", item.price);
        else
            formattedPrice = String.format(Locale.getDefault(), "%.2f %s", item.price, item.currency);
        return formattedPrice;
    }

    public ObservableInt getOrderCount() {
        return orderCount;
    }


    public String getAdditionalInstructions() {
        return additionalInstructions;
    }

    public void onCountUpClicked(View view) {
        orderCount.set(orderCount.get() + 1);
    }

    public void onCountDownClicked(View view) {
        orderCount.set(orderCount.get() - 1);
    }

    public void onAddButtonClicked(View view) {

        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, item);
        intent.putExtra(EXTRA_ITEM_COUNT, orderCount.get());

        activity.setResult(RESULT_ITEM_ADDED, intent);
        for (int i = 0; i < orderCount.get(); i++) {
            sessionManager.addPendingOrder(item, additionalInstructions);
        }

        activity.finishAfterTransition();
    }
}
