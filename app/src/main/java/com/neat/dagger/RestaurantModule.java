package com.neat.dagger;

import com.neat.RestaurantProvider;
import com.neat.RestaurantProviderMock;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RestaurantModule {

    @Provides
    @Singleton
    RestaurantProvider providesRestaurantProvider() {
        return new RestaurantProviderMock();
    }
}
