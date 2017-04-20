package com.neat.viewmodel;

import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import com.neat.model.SessionManager;
import com.neat.model.UserManager;
import com.neat.model.classes.CreditCard;
import com.neat.model.classes.Session;
import com.neat.view.CardEditActivity;
import com.neat.view.PaymentActivity;
import com.neat.view.util.PriceUtil;

import java.util.List;

/**
 * Created by f.gatti.gomez on 21/02/2017.
 */
public class PaymentViewModel implements UserManager.CreditCardsFetchCallback, UserManager.CreditCardCreateCallback, SessionManager.OnPaymentCallbacks {

    private final PaymentActivity paymentActivity;
    private final SessionManager sessionManager;
    private final UserManager userManager;

    private ObservableInt cardSpinnerVisibility = new ObservableInt(View.VISIBLE);
    private ObservableInt selectedCardVisibility = new ObservableInt(View.INVISIBLE);
    private ObservableInt addCardVisibility = new ObservableInt(View.INVISIBLE);

    private ObservableField<CreditCard> selectedCreditCard = new ObservableField<>();

    public PaymentViewModel(PaymentActivity paymentActivity, SessionManager sessionManager, UserManager userManager) {
        this.paymentActivity = paymentActivity;
        this.sessionManager = sessionManager;
        this.userManager = userManager;

        userManager.getCreditCards(this);
        userManager.addCreditCardCreateCallback(this);
        sessionManager.addOnSessionPaidCallbacks(this);
    }
    
    public void destroy(){
        userManager.removeCreditCardCreateCallback(this);
        sessionManager.removeOnSessionPaidCallbacks(this);
    }

    public void onNewCardClicked(View view) {
        Intent intent = new Intent(paymentActivity, CardEditActivity.class);
        paymentActivity.startActivity(intent);
    }

    public void onPayClicked(View view) {
        sessionManager.pay(selectedCreditCard.get());
    }

    public String getRestaurantTitle() {
        return sessionManager.getSession().restaurant.title;
    }

    public String getTotalPrice() {
        return PriceUtil.getFormattedPrice(sessionManager);
    }

    public String getUserName() {
        return userManager.getLoggedInUser().name;
    }

    public ObservableField<CreditCard> getSelectedCreditCard() {
        return selectedCreditCard;
    }

    @Override
    public void onCreditCardsResult(List<CreditCard> cards) {
        cardSpinnerVisibility.set(View.GONE);
        if (cards.isEmpty()) {
            selectedCardVisibility.set(View.GONE);
            addCardVisibility.set(View.VISIBLE);
        } else {
            selectedCreditCard.set(cards.get(0));
            selectedCardVisibility.set(View.VISIBLE);
            addCardVisibility.set(View.GONE);
        }
    }

    @Override
    public void onCreditCardFetchError() {
        cardSpinnerVisibility.set(View.GONE);
        // TODO

    }

    public ObservableInt getCardSpinnerVisibility() {
        return cardSpinnerVisibility;
    }

    public ObservableInt getSelectedCardVisibility() {
        return selectedCardVisibility;
    }

    public ObservableInt getAddCardVisibility() {
        return addCardVisibility;
    }

    @Override
    public void onCreditCardCreated(CreditCard card) {
        selectedCreditCard.set(card);
        selectedCardVisibility.set(View.VISIBLE);
        addCardVisibility.set(View.GONE);
    }

    @Override
    public void onCreditCardCreateError() {

    }

    @Override
    public void onSessionPayed(Session session) {
        paymentActivity.finish();
    }

    @Override
    public void onSessionPaymentFail() {

    }
}
