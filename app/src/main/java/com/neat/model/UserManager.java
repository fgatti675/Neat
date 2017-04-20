package com.neat.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.neat.BuildConfig;
import com.neat.dagger.UserScope;
import com.neat.model.adyenpaysdk.exceptions.EncrypterException;
import com.neat.model.adyenpaysdk.pojo.PaymentData;
import com.neat.model.adyenpaysdk.util.ClientSideEncrypter;
import com.neat.model.adyenpaysdk.util.Luhn;
import com.neat.model.classes.CreditCard;
import com.neat.model.classes.User;

import java.util.ArrayList;
import java.util.List;

import static com.neat.model.FireBasePaths.CARDS;
import static com.neat.model.FireBasePaths.CARD_CARDHOLDERNAME;
import static com.neat.model.FireBasePaths.CARD_ENCRYPTED_TOKEN;
import static com.neat.model.FireBasePaths.CARD_EXPIRYMONTH;
import static com.neat.model.FireBasePaths.CARD_EXPIRYYEAR;
import static com.neat.model.FireBasePaths.CARD_NUMBER_LAST_DIGITS;
import static com.neat.model.FireBasePaths.CARD_TYPE;
import static com.neat.model.FireBasePaths.CREATION_DATE;
import static com.neat.model.FireBasePaths.USERS;


/**
 * Created by f.gatti.gomez on 02/02/2017.
 */
@UserScope
public class UserManager {

    private User loggedUser;

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    List<CreditCardCreateCallback> creditCardCreateCallbacks = new ArrayList<>();

    public User getLoggedInUser() {

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

    public interface CreditCardCreateCallback {
        void onCreditCardCreated(CreditCard card);

        void onCreditCardCreateError();
    }

    public interface CreditCardsFetchCallback {
        void onCreditCardsResult(List<CreditCard> cards);

        void onCreditCardFetchError();
    }

    public interface TokenFetchCallback {
        void onTokenFetchedResult(String token);

        void onTokenFetchError(Exception e);
    }

    public void createCreditCard(final PaymentData paymentData) {

        if (getLoggedInUser() == null)
            throw new IllegalStateException("User must be logged in to create a credit card");

        if (!Luhn.check(paymentData.number))
            throw new IllegalArgumentException("Card doesn;t pass Luhn check");

        try {
            ClientSideEncrypter encrypter = new ClientSideEncrypter(BuildConfig.ADYEN_PUBLIC_KEY);
            final String encriptedCard = encrypter.encrypt(paymentData.toJsonObject().toString());

            database.child(USERS).child(getLoggedInUser().uid).child(CARDS).push().runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    mutableData.child(CARD_NUMBER_LAST_DIGITS).setValue(paymentData.number.substring(paymentData.number.length() - 4));
                    mutableData.child(CARD_CARDHOLDERNAME).setValue(paymentData.cardHolderName);
                    mutableData.child(CARD_EXPIRYMONTH).setValue(paymentData.expiryMonth);
                    mutableData.child(CARD_EXPIRYYEAR).setValue(paymentData.expiryYear);
                    mutableData.child(CREATION_DATE).setValue(ServerValue.TIMESTAMP);
                    mutableData.child(CARD_ENCRYPTED_TOKEN).setValue(encriptedCard);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    if (databaseError != null) {
                        for (CreditCardCreateCallback callback : creditCardCreateCallbacks)
                            callback.onCreditCardCreateError();
                        return;
                    }
                    for (CreditCardCreateCallback callback : creditCardCreateCallbacks)
                        callback.onCreditCardCreated(convertCreditCardFrom(dataSnapshot));
                }
            });
        } catch (EncrypterException e) {
            e.printStackTrace();
        }


    }

    public void getCreditCards(final CreditCardsFetchCallback callback) {

        if (getLoggedInUser() == null)
            throw new IllegalStateException("User must be logged in to fetch credit cards");

        database.child(USERS).child(getLoggedInUser().uid).child(CARDS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<CreditCard> cards = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    cards.add(convertCreditCardFrom(child));
                }
                callback.onCreditCardsResult(cards);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCreditCardFetchError();
            }
        });
    }

    private static CreditCard convertCreditCardFrom(DataSnapshot dataSnapshot) {
        CreditCard order = new CreditCard();
        order.numberLastDigits = (String) dataSnapshot.child(CARD_NUMBER_LAST_DIGITS).getValue();
        order.encryptedToken = (String) dataSnapshot.child(CARD_ENCRYPTED_TOKEN).getValue();
        order.expiryMonth = (String) dataSnapshot.child(CARD_EXPIRYMONTH).getValue();
        order.expiryYear = (String) dataSnapshot.child(CARD_EXPIRYYEAR).getValue();
        order.cardHolderName = (String) dataSnapshot.child(CARD_CARDHOLDERNAME).getValue();
        order.type = (String) dataSnapshot.child(CARD_TYPE).getValue();
        order.generationTime = (Long) dataSnapshot.child(CREATION_DATE).getValue();
        return order;
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
    }

    public void addCreditCardCreateCallback(CreditCardCreateCallback creditCardCreateCallback) {
        creditCardCreateCallbacks.add(creditCardCreateCallback);
    }

    public void removeCreditCardCreateCallback(CreditCardCreateCallback creditCardCreateCallback) {
        creditCardCreateCallbacks.remove(creditCardCreateCallback);
    }

    public void getUserToken(final TokenFetchCallback callback) {
        FirebaseAuth.getInstance().getCurrentUser().getToken(false)
                .addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                    @Override
                    public void onSuccess(GetTokenResult getTokenResult) {
                        final String idToken = getTokenResult.getToken();
                        callback.onTokenFetchedResult(idToken);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onTokenFetchError(e);
                    }
                });
    }
}
