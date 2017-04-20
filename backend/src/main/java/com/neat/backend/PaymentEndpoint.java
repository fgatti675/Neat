package com.neat.backend;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.tasks.OnSuccessListener;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by f.gatti.gomez on 12/03/2017.
 */
@Path("/")
public class PaymentEndpoint {

    @GET
    @Produces("text/plain")
    public Response getText(@HeaderParam("Authentication") String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Wrong auth header");
        }

        String token = authHeader.substring("Bearer ".length());
        FirebaseAuth.getInstance().verifyIdToken(token)
                .addOnSuccessListener(new OnSuccessListener<FirebaseToken>() {
                    @Override
                    public void onSuccess(FirebaseToken decodedToken) {
                        String uid = decodedToken.getUid();
                        // ...
                    }
                });

        return Response.ok("Hello world!").build();
    }

}
