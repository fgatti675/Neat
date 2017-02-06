package com.neat.dagger;

import com.neat.view.MenuActivity;
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

    void inject(MenuActivity menuActivity);

    void inject(OrdersFragment fragment);

    void inject(ItemListSmallFragment fragment);

    void inject(ItemFeaturedFragment fragment);

    void inject(ItemListFragment fragment);

//    OrdersFragmentComponent plus();


}
