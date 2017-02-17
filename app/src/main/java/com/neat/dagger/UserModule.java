package com.neat.dagger;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neat.model.classes.User;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by f.gatti.gomez on 06/01/2017.
 */
@Module
public class UserModule {

    User loggedUser;

    @Provides
    @Singleton
    @Named("logged_user")
    User providesLoggedInUser() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return null;

        if (loggedUser == null) {
            loggedUser = new User();
            loggedUser.uid = firebaseUser.getUid();
            loggedUser.email = firebaseUser.getEmail();
            loggedUser.name = firebaseUser.getDisplayName();
            Uri photoUrl = firebaseUser.getPhotoUrl();
            if (photoUrl != null)
                loggedUser.photoUrl = photoUrl.toString();
        }

        return loggedUser;
    }
}