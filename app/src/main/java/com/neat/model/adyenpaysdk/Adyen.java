package com.neat.model.adyenpaysdk;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.neat.model.adyenpaysdk.exceptions.EncrypterException;
import com.neat.model.adyenpaysdk.exceptions.NoPublicKeyExeption;
import com.neat.model.adyenpaysdk.pojo.PaymentData;
import com.neat.model.adyenpaysdk.util.ClientSideEncrypter;
import com.neat.model.adyenpaysdk.util.Luhn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by andrei on 11/5/15.
 */
public class Adyen {

    private static Adyen mInstance = null;

    private static final String tag = Adyen.class.getSimpleName();

    private boolean useTestBackend = true;
    private String token;
    private String publicKey;

    OkHttpClient client = new OkHttpClient();

    public interface CompletionCallback {

        void onSuccess(String publicKey);

        void onError(String error);

    }

    private Adyen() {

    }

    public static Adyen getInstance() {
        if (mInstance == null) {
            mInstance = new Adyen();
        }
        return mInstance;
    }

    public void fetchPublicKey(final CompletionCallback completion) {
        String host = (useTestBackend) ? "test" : "live";
        final String url = String.format("https://%s.adyen.com/hpp/cse/%s/json.shtml", host, token);

        new AsyncTask<String, Void, JSONObject>() {
            private static final String SUCCESS = "ok";

            @Override
            protected JSONObject doInBackground(String... urls) {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    return new JSONObject(response.body().string());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(JSONObject result) {
                if (result == null) completion.onError(null);

                String status = null;
                try {
                    status = result.getString("status");
                    if (SUCCESS.equals(status)) {
                        try {
                            publicKey = result.getString("publicKey");
                            completion.onSuccess(publicKey);

                        } catch (JSONException e) {
                            Log.e(tag, e.getMessage(), e);
                            completion.onError(e.getMessage());
                        }
                    } else {
                        completion.onError(status);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public String encryptData(String data) throws NoPublicKeyExeption, EncrypterException {
        String encryptedData = null;
        if (!TextUtils.isEmpty(publicKey)) {
            try {
                ClientSideEncrypter encrypter = new ClientSideEncrypter(publicKey);
                encryptedData = encrypter.encrypt(data);
            } catch (EncrypterException e) {
                throw e;
            }
        } else {
            throw new NoPublicKeyExeption("No public key was found!");
        }

        return encryptedData;
    }

    public String serialize(PaymentData creditCard) throws EncrypterException, NoPublicKeyExeption {
        JSONObject cardJson = creditCard.toJsonObject();
        String encryptedData = encryptData(cardJson.toString());
        return encryptedData;
    }

    public boolean luhnCheck(String cardNumber) {
        return Luhn.check(cardNumber);
    }

    public boolean isUseTestBackend() {
        return useTestBackend;
    }

    public void setUseTestBackend(boolean useTestBackend) {
        this.useTestBackend = useTestBackend;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
