package com.neat.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;

import com.neat.fragments.OrdersFragment;
import com.neat.model.Item;

/**
 * Created by f.gatti.gomez on 10/11/2016.
 */

public class AddItemToOrdersHandler {

    public void onItemClick(View view, Item item){
        Context context = view.getContext();
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            FragmentManager fragmentManager = activity.getFragmentManager();
            OrdersFragment ordersFragment = (OrdersFragment) fragmentManager.findFragmentByTag(OrdersFragment.FRAGMENT_TAG);
            ordersFragment.addPendingItem(item);
        }
    }

}
