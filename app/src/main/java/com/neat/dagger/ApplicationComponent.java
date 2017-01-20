package com.neat.dagger;


import com.neat.NeatApplication;
import com.neat.RestaurantActivity;
import com.neat.fragments.OrdersFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RestaurantModule.class, SessionManagerModule.class})
public interface ApplicationComponent {

    void inject(RestaurantActivity activity);

    void inject(OrdersFragment fragment);

    NeatApplication application();
}
