package com.neat.model.classes;

import java.io.Serializable;

/**
 * Created by andrei on 11/5/15.
 */
public class CreditCard implements Serializable {

    public String id;
    public String numberLastDigits;
    public String expiryMonth;
    public String expiryYear;
    public String cardHolderName;
    public String type;
    public Long generationTime;
    public String encryptedToken;
}
