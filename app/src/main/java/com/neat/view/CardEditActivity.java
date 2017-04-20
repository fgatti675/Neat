package com.neat.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cooltechworks.creditcarddesign.CardSelector;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.neat.NeatApplication;
import com.neat.R;
import com.neat.dagger.UserScope;
import com.neat.model.UserManager;
import com.neat.model.adyenpaysdk.pojo.PaymentData;
import com.neat.model.classes.CreditCard;
import com.neat.view.cardedit.CardFragmentAdapter;
import com.neat.view.cardedit.CreditCardFragment;

import java.util.Date;

import javax.inject.Inject;

import static com.cooltechworks.creditcarddesign.CreditCardUtils.CARD_NAME_PAGE;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD_CVV;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD_EXPIRY;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD_HOLDER_NAME;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_ENTRY_START_PAGE;

@UserScope
public class CardEditActivity extends AppCompatActivity implements UserManager.CreditCardCreateCallback {

    @Inject
    UserManager userManager;

    int mLastPageSelected = 0;
    private CreditCardView mCreditCardView;

    private String mCardNumber;
    private String mCVV;
    private String mCardHolderName;
    private String mExpiry;
    private int mStartPage = 0;
    private CardFragmentAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        NeatApplication.getComponent(this).application().getUserComponent().inject(this);

        setContentView(R.layout.activity_card_edit);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewPager pager = (ViewPager) findViewById(R.id.card_field_container_pager);

                int max = pager.getAdapter().getCount();

                if (pager.getCurrentItem() == max - 1) {
                    // if last card.
                    onDoneTapped();
                } else {
                    showNext();
                }
            }
        });
        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrevious();
            }
        });

        setKeyboardVisibility(true);
        mCreditCardView = (CreditCardView) findViewById(R.id.credit_card_view);
        Bundle args = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();

        checkParams(args);
        loadPager(args);

        if (mStartPage > 0 && mStartPage <= CARD_NAME_PAGE) {
            getViewPager().setCurrentItem(mStartPage);
        }
        userManager.addCreditCardCreateCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userManager.removeCreditCardCreateCallback(this);
    }

    private void checkParams(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        mCardHolderName = bundle.getString(EXTRA_CARD_HOLDER_NAME);
        mCVV = bundle.getString(EXTRA_CARD_CVV);
        mExpiry = bundle.getString(EXTRA_CARD_EXPIRY);
        mCardNumber = bundle.getString(EXTRA_CARD);
        mStartPage = bundle.getInt(EXTRA_ENTRY_START_PAGE);

        int maxCvvLength = CardSelector.selectCard(mCardNumber).getCvvLength();
        if (mCVV != null && mCVV.length() >= maxCvvLength) {
            mCVV = mCVV.substring(0, maxCvvLength);
        }

        mCreditCardView.setCVV(mCVV);
        mCreditCardView.setCardHolderName(mCardHolderName);
        mCreditCardView.setCardExpiry(mExpiry);
        mCreditCardView.setCardNumber(mCardNumber);

        if (mCardAdapter != null) {
            mCardAdapter.setMaxCVV(maxCvvLength);
            mCardAdapter.notifyDataSetChanged();
        }
    }

    public void refreshNextButton() {
        ViewPager pager = (ViewPager) findViewById(R.id.card_field_container_pager);

        int max = pager.getAdapter().getCount();

        int text = R.string.next;

        if (pager.getCurrentItem() == max - 1) {
            text = R.string.done;
        }

        ((TextView) findViewById(R.id.next)).setText(text);
    }

    ViewPager getViewPager() {
        return (ViewPager) findViewById(R.id.card_field_container_pager);
    }

    public void loadPager(Bundle bundle) {
        ViewPager pager = getViewPager();
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                CreditCardFragment fragment = (CreditCardFragment) mCardAdapter.getItem(position);
                findViewById(R.id.next).setEnabled(fragment.isInputValid());
                mCardAdapter.focus(position);

                if (position == 2) {
                    mCreditCardView.showBack();
                } else if ((position == 1 && mLastPageSelected == 2) || position == 3) {
                    mCreditCardView.showFront();
                }

                mLastPageSelected = position;

                refreshNextButton();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pager.setOffscreenPageLimit(4);

        mCardAdapter = new CardFragmentAdapter(getSupportFragmentManager(), bundle);
        mCardAdapter.setOnCardEntryCompleteListener(new CardFragmentAdapter.ICardEntryCompleteListener() {
            @Override
            public void onCardEntryComplete(int currentIndex) {
                showNext();
            }

            @Override
            public void onCardEntryEdit(int currentIndex, String entryValue, boolean validValue) {
                findViewById(R.id.next).setEnabled(validValue);
                switch (currentIndex) {
                    case 0:
                        mCardNumber = entryValue.replace(CreditCardUtils.SPACE_SEPERATOR, "");
                        mCreditCardView.setCardNumber(mCardNumber);
                        if (mCardAdapter != null) {
                            mCardAdapter.setMaxCVV(CardSelector.selectCard(mCardNumber).getCvvLength());
                        }
                        break;
                    case 1:
                        mExpiry = entryValue;
                        mCreditCardView.setCardExpiry(entryValue);
                        break;
                    case 2:
                        mCVV = entryValue;
                        mCreditCardView.setCVV(entryValue);
                        break;
                    case 3:
                        mCardHolderName = entryValue;
                        mCreditCardView.setCardHolderName(entryValue);
                        break;
                }
            }
        });

        pager.setAdapter(mCardAdapter);

        int cardSide = getIntent().getIntExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_FRONT);
        if (cardSide == CreditCardUtils.CARD_SIDE_BACK) {
            pager.setCurrentItem(2);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_CARD_CVV, mCVV);
        outState.putString(EXTRA_CARD_HOLDER_NAME, mCardHolderName);
        outState.putString(EXTRA_CARD_EXPIRY, mExpiry);
        outState.putString(EXTRA_CARD, mCardNumber);

        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        checkParams(inState);
    }


    public void showPrevious() {
        final ViewPager pager = (ViewPager) findViewById(R.id.card_field_container_pager);
        int currentIndex = pager.getCurrentItem();

        if (currentIndex == 0) {
            setResult(RESULT_CANCELED);
            finish();
        }

        if (currentIndex - 1 >= 0) {
            pager.setCurrentItem(currentIndex - 1);
        }

        refreshNextButton();
    }

    public void showNext() {
        final ViewPager pager = (ViewPager) findViewById(R.id.card_field_container_pager);
        CardFragmentAdapter adapter = (CardFragmentAdapter) pager.getAdapter();

        int max = adapter.getCount();
        int currentIndex = pager.getCurrentItem();

        if (currentIndex + 1 < max) {

            pager.setCurrentItem(currentIndex + 1);
        } else {
            // completed the card entry.
            setKeyboardVisibility(false);
        }

        refreshNextButton();
    }

    private void onDoneTapped() {

        PaymentData paymentData = new PaymentData();
        paymentData.cardHolderName = mCardHolderName;
        paymentData.expiryMonth = mExpiry.substring(0, 2);
        paymentData.expiryYear = "20" + mExpiry.substring(3, 5);
        paymentData.number = mCardNumber;
        paymentData.cvc = mCVV;
        paymentData.generationTime = new Date();

        userManager.createCreditCard(paymentData);

    }

    @Override
    public void onCreditCardCreated(CreditCard card) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_CARD, card);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCreditCardCreateError() {

    }

    // from the link above
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            finish();
        }
    }

    private void setKeyboardVisibility(boolean visible) {
        final EditText editText = (EditText) findViewById(R.id.card_number_field);

        if (!visible) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
