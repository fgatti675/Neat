package com.neat;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.neat.dagger.AppModule;
import com.neat.dagger.ApplicationComponent;
import com.neat.dagger.DaggerApplicationComponent;
import com.neat.dagger.SessionComponent;
import com.neat.dagger.SessionModule;
import com.neat.dagger.UserComponent;
import com.neat.dagger.UserModule;

/**
 * Created by f.gatti.gomez on 06/01/2017.
 */

public class NeatApplication extends Application {

    private ApplicationComponent applicationComponent;
    private SessionComponent sessionComponent;
    private UserComponent userComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .appModule(new AppModule(this))
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());

    }

    public static ApplicationComponent getComponent(Context context) {
        return ((NeatApplication) context.getApplicationContext()).applicationComponent;
    }

    public UserComponent createUserComponent() {
        userComponent = applicationComponent.plus(new UserModule());
        return userComponent;
    }

    public SessionComponent createSessionComponent() {
        sessionComponent = userComponent.plus(new SessionModule());
        return sessionComponent;
    }

    public SessionComponent getSessionComponent() {
        return sessionComponent;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }
}
