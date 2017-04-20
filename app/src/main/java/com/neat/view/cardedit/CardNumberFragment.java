package com.neat.view.cardedit;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.R;
import com.neat.model.adyenpaysdk.util.Luhn;

import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.MAX_LENGTH_CARD_NUMBER;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.MAX_LENGTH_CARD_NUMBER_WITH_SPACES;

/**
 * Created by sharish on 9/1/15.
 */
public class CardNumberFragment extends CreditCardFragment {

    EditText mCardNumberView;

    public CardNumberFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle state) {
        View v = inflater.inflate(R.layout.lyt_card_number, group, false);
        mCardNumberView = (EditText) v.findViewById(R.id.card_number_field);

        String number = "";

        if (getArguments() != null && getArguments().containsKey(EXTRA_CARD)) {
            number = getArguments().getString(EXTRA_CARD);
        }

        if (number == null) {
            number = "";
        }

        mCardNumberView.setText(number);
        mCardNumberView.addTextChangedListener(this);

        return v;
    }


    @Override
    public void afterTextChanged(Editable s) {
        int cursorPosition = mCardNumberView.getSelectionEnd();
        int previousLength = mCardNumberView.getText().length();

        String cardNumber = CreditCardUtils.handleCardNumber(s.toString());
        int modifiedLength = cardNumber.length();

        mCardNumberView.removeTextChangedListener(this);
        mCardNumberView.setText(cardNumber);
        mCardNumberView.setSelection(cardNumber.length() > MAX_LENGTH_CARD_NUMBER_WITH_SPACES ? MAX_LENGTH_CARD_NUMBER_WITH_SPACES : cardNumber.length());
        mCardNumberView.addTextChangedListener(this);

        if (modifiedLength <= previousLength && cursorPosition < modifiedLength) {
            mCardNumberView.setSelection(cursorPosition);
        }

        boolean inputValid = isInputValid();
        onEdit(cardNumber, inputValid);

        String cardString = cardNumber.replace(CreditCardUtils.SPACE_SEPERATOR, "");
        if (cardString.length() == MAX_LENGTH_CARD_NUMBER) {
            if (inputValid)
                onComplete();
            else
                mCardNumberView.setError(getString(R.string.error_invalid_card));
        }
    }

    @Override
    public void focus() {
        if (isAdded()) {
            mCardNumberView.selectAll();
        }
    }

    @Override
    public boolean isInputValid() {
        String cardString = mCardNumberView.getText().toString().replace(CreditCardUtils.SPACE_SEPERATOR, "");
        return cardString.length() == MAX_LENGTH_CARD_NUMBER && Luhn.check(cardString);
    }
}
