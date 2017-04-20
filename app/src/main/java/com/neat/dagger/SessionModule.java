package com.neat.dagger;

import com.neat.model.SessionManager;
import com.neat.model.UserManager;
import com.neat.model.classes.User;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class SessionModule {

    @Provides
    @SessionScope
    SessionManager providesSessionManager(@Named("logged_user") User user, UserManager userManager) {
        return new SessionManager(user, userManager);
    }


}
