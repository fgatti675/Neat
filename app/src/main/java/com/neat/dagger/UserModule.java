package com.neat.dagger;

import com.neat.model.UserManager;
import com.neat.model.classes.User;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by f.gatti.gomez on 06/01/2017.
 */
@Module
public class UserModule {

    @Provides
    @UserScope
    @Named("logged_user")
    User providesLoggedInUser() {
        return getUserManager().getLoggedInUser();
    }

    @UserScope
    @Provides
    UserManager getUserManager() {
        return new UserManager();
    }
}