package com.neat.dagger;

import com.neat.NeatApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by f.gatti.gomez on 06/01/2017.
 */
@Module
public class AppModule {

    NeatApplication mApplication;

    public AppModule(NeatApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    NeatApplication providesApplication() {
        return mApplication;
    }
}