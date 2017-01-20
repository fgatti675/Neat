package com.neat.dagger;

import com.neat.SessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SessionManagerModule {

    @Provides
    @Singleton
    SessionManager providesSessionManager() {
        return new SessionManager();
    }
}
