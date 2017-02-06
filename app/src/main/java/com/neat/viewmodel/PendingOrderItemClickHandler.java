package com.neat.viewmodel;

import android.view.View;

import com.neat.model.classes.Order;

/**
 * Created by f.gatti.gomez on 10/11/2016.
 */

public interface PendingOrderItemClickHandler {

    void onOrderClick(View view, Order order);

    void onOrderRemovedClick(View view, Order order);

}
