package com.neat.dagger;


import com.neat.NeatApplication;
import com.neat.model.RestaurantProvider;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class, RestaurantModule.class})
public interface ApplicationComponent {

    RestaurantProvider restaurantProvider();

    NeatApplication application();

    UserComponent plus(UserModule userModule);

}
