package com.neat.dagger;

import com.neat.RestaurantProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RestaurantModule {

    @Provides
    @Singleton
    RestaurantProvider providesRestaurantProvider() {
        return new RestaurantProvider();
    }
}
