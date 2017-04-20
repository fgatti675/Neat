package com.neat.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Created by f.gatti.gomez on 12/03/2017.
 */
@ApplicationPath("/")
public class JerseyApp extends ResourceConfig {
    public JerseyApp() {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(getClass().getClassLoader().getResourceAsStream("WEB-INF/neat-eb7c6-firebase-adminsdk-edcz4-8078dde852.json"))
                .setDatabaseUrl("https://neat-eb7c6.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        packages(this.getClass().getPackage().getName());
    }
}
