package com.cahue.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;

import com.cahue.fragments.OrdersFragment;
import com.cahue.model.Item;

/**
 * Created by f.gatti.gomez on 10/11/2016.
 */

public class ItemClickHandler {

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
