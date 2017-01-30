package com.neat.util;

import android.view.View;

import com.neat.model.Item;

/**
 * Created by f.gatti.gomez on 10/11/2016.
 */

public interface AddItemToOrdersHandler {

    void onItemClick(View view, Item item);

    void onDirectAddItemButtonClicked(View view, Item item);

}
