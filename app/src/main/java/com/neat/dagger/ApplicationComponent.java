package com.neat.dagger;


import com.neat.ItemDetailsFragment;
import com.neat.NeatApplication;
import com.neat.RestaurantActivity;
import com.neat.fragments.ItemFeaturedFragment;
import com.neat.fragments.ItemListFragment;
import com.neat.fragments.ItemListSmallFragment;
import com.neat.fragments.OrdersFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RestaurantModule.class, SessionManagerModule.class})
public interface ApplicationComponent {

    void inject(RestaurantActivity activity);

    void inject(OrdersFragment fragment);

    void inject(ItemListFragment fragment);

    void inject(ItemFeaturedFragment fragment);

    void inject(ItemListSmallFragment fragment);

    void inject(ItemDetailsFragment fragment);

    NeatApplication application();
}
