package com.neat;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.neat.dagger.AppModule;
import com.neat.dagger.ApplicationComponent;
import com.neat.dagger.DaggerApplicationComponent;
import com.neat.dagger.RestaurantModule;
import com.neat.dagger.SessionManagerModule;

/**
 * Created by f.gatti.gomez on 06/01/2017.
 */

public class NeatApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(this))
                .restaurantModule(new RestaurantModule())
                .sessionManagerModule(new SessionManagerModule())
                .build();

        FacebookSdk.sdkInitialize(getApplicationContext());

        // If a Dagger 2 component does not have any constructor arguments for any of its modules,
        // then we can use .create() as a shortcut instead:
        //  mNetComponent = com.codepath.dagger.components.DaggerNetComponent.create();
    }

    public static ApplicationComponent getComponent(Context context) {
        return ((NeatApplication) context.getApplicationContext()).applicationComponent;
    }

}
