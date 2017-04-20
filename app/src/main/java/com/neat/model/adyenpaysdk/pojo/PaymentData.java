package com.neat.model.adyenpaysdk.pojo;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by andrei on 11/5/15.
 */
public class PaymentData {

    private static final String tag = PaymentData.class.getSimpleName();

    public String number;
    public String expiryMonth;
    public String expiryYear;
    public String cardHolderName;
    public String cvc;
    public Date generationTime;

    @NonNull
    public JSONObject toJsonObject() {
        JSONObject cardJson = new JSONObject();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            cardJson.put("generationtime", simpleDateFormat.format(generationTime));
            cardJson.put("number", number);
            cardJson.put("holderName", cardHolderName);
            cardJson.put("cvc", cvc);
            cardJson.put("expiryMonth", expiryMonth);
            cardJson.put("expiryYear", expiryYear);
        } catch (JSONException e) {
            Log.e(tag, e.getMessage(), e);
        }
        return cardJson;
    }

    @Override
    public String toString() {
        JSONObject cardJson = new JSONObject();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            cardJson.put("generationtime", simpleDateFormat.format(generationTime));
            if (number.length() >= 4) {
                cardJson.put("number", number.substring(0, 3));
            }
            cardJson.put("holderName", cardHolderName);
        } catch (JSONException e) {
            Log.e(tag, e.getMessage(), e);
        }

        return cardJson.toString();
    }
}
