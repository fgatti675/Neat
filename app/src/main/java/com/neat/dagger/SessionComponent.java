package com.neat.dagger;

import com.neat.view.PaymentActivity;
import com.neat.view.ItemDetailsActivity;
import com.neat.view.RestaurantSessionActivity;
import com.neat.view.fragments.ItemFeaturedFragment;
import com.neat.view.fragments.ItemListFragment;
import com.neat.view.fragments.ItemListSmallFragment;
import com.neat.view.fragments.OrdersFragment;

import dagger.Subcomponent;

/**
 * Created by f.gatti.gomez on 02/02/2017.
 */
@SessionScope
@Subcomponent(modules = {SessionModule.class})
public interface SessionComponent {

    void inject(RestaurantSessionActivity restaurantSessionActivity);

    void inject(OrdersFragment fragment);

    void inject(ItemListSmallFragment fragment);

    void inject(ItemFeaturedFragment fragment);

    void inject(ItemListFragment fragment);

    void inject(ItemDetailsActivity itemDetailsActivity);

    void inject(PaymentActivity paymentActivity);


}
