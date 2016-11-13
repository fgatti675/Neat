package com.cahue.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cahue.R;
import com.cahue.model.Bill;
import com.cahue.model.Item;
import com.cahue.model.Order;
import com.cahue.util.BindingUtils;
import com.google.android.flexbox.FlexboxLayout;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;


public class OrdersFragment extends Fragment {

    public static final String FRAGMENT_TAG = "ORDERS_FRAGMENT";

    private static final String TAG = OrdersFragment.class.getSimpleName();

    private Bill bill;

    @Bind(R.id.empty_text)
    TextView emptyTextView;

    @Bind(R.id.items_layout)
    FlexboxLayout itemsLayout;

    @Bind(R.id.caret)
    View caret;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.orders_scrollview)
    NestedScrollView scrollView;

    @Bind(R.id.modal_bg)
    View modalBg;

    @Bind(R.id.orders_layout)
    ConstraintLayout ordersLayout;

    private LayoutInflater inflater;

    @Inject
    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, view);

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                coordinatorLayout.setPadding();
                Log.d(TAG, "onScrollChange: " + scrollX);

            }
        });

        final float topMargin = getResources().getDimension(R.dimen.orders_layout_top_margin);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(scrollView);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                caret.setRotation(slideOffset * 180);
                modalBg.setAlpha(slideOffset * .9F);
                bottomSheet.setPadding(0, (int) (topMargin * slideOffset), 0, 0);
                if(slideOffset > 0){
                    beginOrdersTransition();
                }
            }
        });

        // we need to enforce programmatically minimum height of bottom layout
        View bottomLayout = view.findViewById(R.id.bottom_layout);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        bottomLayout.setMinimumHeight(size.y - (int) topMargin);

        return view;
    }

    private void beginOrdersTransition() {
        TransitionManager.beginDelayedTransition(ordersLayout);
        itemsLayout.setFlexDirection(FlexboxLayout.FLEX_DIRECTION_ROW);
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public void addPendingItem(Item item) {
        Order order = new Order();
        order.status = Order.Status.pending;
        order.item = item;
        bill.orders.add(order);
        Integer iconDrawableRes = BindingUtils.getIconDrawableRes(item);
        emptyTextView.setVisibility(View.GONE);
        itemsLayout.setVisibility(View.VISIBLE);

//        ImageView itemIcon = new ImageView(getActivity());
//        itemIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int iconSide = (int) getResources().getDimension(R.dimen.small_icon);
//        FlexboxLayout.LayoutParams  params = new FlexboxLayout.LayoutParams (iconSide, iconSide);
//        params.maxHeight = iconSide;
//        int tinyPadding = (int) getResources().getDimension(R.dimen.tiny_padding);
//        params.setMargins(tinyPadding, 0, tinyPadding, 0);
//        itemIcon.setLayoutParams(params);
        ImageView itemIcon = (ImageView) inflater.inflate(R.layout.icon_imageview, itemsLayout, false);

        itemIcon.setImageDrawable(getResources().getDrawable(iconDrawableRes));
        itemsLayout.addView(itemIcon);

        // get the final radius for the clipping circle
        int finalRadius = (int) (iconSide / 2 * 1.44);
        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(itemIcon, iconSide / 2, iconSide / 2, 0, finalRadius);
        anim.start();
//        Animator iconBounce = AnimatorInflater.loadAnimator(getActivity(), R.animator.icon_bounce_animator);
//        iconBounce.setTarget(itemIcon);

        Animator bounce = AnimatorInflater.loadAnimator(getActivity(), R.animator.orders_bounce_animator);
        bounce.setTarget(getView());
        bounce.start();

    }
}
