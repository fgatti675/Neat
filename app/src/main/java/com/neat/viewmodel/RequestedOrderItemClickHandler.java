package com.neat.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.neat.model.classes.Order;

/**
 * Created by f.gatti.gomez on 10/11/2016.
 */

public class RequestedOrderItemClickHandler {

    public void onOrderClick(View view, Order order){
        Context context = view.getContext();
        if (context instanceof Activity) {
        }

    }

}
