package com.neat.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.neat.NeatApplication;
import com.neat.R;
import com.neat.SessionManager;
import com.neat.databinding.ItemListPendingOrderBinding;
import com.neat.databinding.ItemListPendingOrderCollapsedBinding;
import com.neat.databinding.ItemListRequestedOrderBinding;
import com.neat.model.Item;
import com.neat.model.Order;
import com.neat.util.PendingOrderItemClickHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.sephiroth.android.library.tooltip.Tooltip;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static com.neat.R.id.caret;

public class OrdersFragment extends Fragment implements SessionManager.OnOrdersPlacedListener, SessionManager.OnNewPendingOrderAddedListener {

    private static final String TAG = OrdersFragment.class.getSimpleName();
    public static final int ORDERS_BUTTON_TOOLTIP_ID = 101;

    @Inject
    SessionManager sessionManager;

    @Bind(R.id.pending_orders_wrapper)
    ViewGroup pendingOrdersWrapper;

    @Bind(R.id.requested_orders_wrapper)
    ViewGroup requestedOrdersWrapper;

    @Bind(R.id.orders_scrollview)
    NestedScrollView ordersScrollView;

    @Bind(R.id.bottom_layout)
    ViewGroup bottomLayout;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.modal_bg)
    View modalBg;

    @Bind(R.id.pending_items_layout)
    ViewGroup pendingItemsLayoutCollapsed;

    @Bind(R.id.requested_orders_empty_view)
    ViewGroup requestedItemsEmptyView;

    ViewGroup requestedItemsLayout;

    @Bind(R.id.order_button)
    Button orderButton;

    @Bind(R.id.pay_button)
    FloatingActionButton payButton;

    // this is a reference to the view that gets displayed when the orders view is collapsed, and can be one the 2 below
    ViewGroup collapsedViewRef;

    TextView pendingOrdersCollapsedTitle;
    TextView pendingOrdersCollapsedSubtitle;
    ImageView pendingImageCollapsed;

    @Bind(R.id.collapsed_orders_layout)
    ViewGroup collapsedOrdersLayout;
    ViewGroup collapsedOrdersEmptyLayout;

    // this is a reference to the view that gets displayed when the orders view is expanded, and can be one the 3 below
    ViewGroup pendingExpandedViewRef;

    // the 3 views that can be displayed when the layout is expanded
    ViewGroup pendingExpandedWithItemsView;
    ViewGroup pendingExpandedEmptyView;
    ViewGroup pendingExpandedJustOrderedView;

    // container for he items themselves
    ViewGroup pendingItemsLayoutExpanded;

    Button orderButtonExpanded;
    ViewGroup requestedOrdersView;

    TextView totalPriceTextView;

    boolean expanded = false;

    private BottomSheetBehavior bottomSheetBehavior;

    private float bottomSheetHeight;
    private CountDownTimer clearJustOrderedStateTimer;
    private Handler handler = new Handler();

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        NeatApplication.getComponent(context).inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        sessionManager.addOnOrdersPlacedListener(this);
        sessionManager.addOnNewPendingOrderAddedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        sessionManager.removeOnOrdersPlacedListener(this);
        sessionManager.removeOnNewPendingOrderAddedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setBottomSheetCollapsedIfHasOrders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, view);

        collapsedViewRef = collapsedOrdersLayout;

        bottomSheetHeight = getResources().getDimension(R.dimen.orders_layout_sneak_peak);

        bottomSheetBehavior = BottomSheetBehavior.from(ordersScrollView);

        // create and bind expanded views ans scene
        pendingExpandedWithItemsView = (ViewGroup) inflater.inflate(R.layout.layout_pending_orders_expanded, pendingOrdersWrapper, false);
        pendingItemsLayoutExpanded = (ViewGroup) pendingExpandedWithItemsView.findViewById(R.id.pending_items_layout);
        orderButtonExpanded = (Button) pendingExpandedWithItemsView.findViewById(R.id.order_button);

        // initially set the expanded view reference as empty
        pendingExpandedViewRef = pendingExpandedEmptyView;

        // create expanded empty view and scene
        pendingExpandedEmptyView = (ViewGroup) inflater.inflate(R.layout.layout_pending_orders_expanded_empty, pendingOrdersWrapper, false);
        pendingExpandedJustOrderedView = (ViewGroup) inflater.inflate(R.layout.layout_pending_orders_expanded_ordered, pendingOrdersWrapper, false);

        requestedOrdersView = (ViewGroup) inflater.inflate(R.layout.layout_requested_orders, requestedOrdersWrapper, false);
        requestedItemsLayout = (ViewGroup) requestedOrdersView.findViewById(R.id.requested_items_layout);
        totalPriceTextView = (TextView) requestedOrdersView.findViewById(R.id.total_amount_view);

        collapsedOrdersEmptyLayout = (ViewGroup) inflater.inflate(R.layout.layout_orders_collapsed_empty_queue, pendingOrdersWrapper, false);

        pendingOrdersCollapsedTitle = (TextView) collapsedOrdersEmptyLayout.findViewById(R.id.collapsed_orders_title);
        pendingOrdersCollapsedSubtitle = (TextView) collapsedOrdersEmptyLayout.findViewById(R.id.collapsed_orders_subtitle);
        pendingImageCollapsed = (ImageView) collapsedOrdersEmptyLayout.findViewById(R.id.collapsed_orders_image);

        // Collapse on caret click
        View.OnClickListener collapseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);
            }
        };
        pendingExpandedWithItemsView.findViewById(caret).setOnClickListener(collapseListener);
        pendingExpandedEmptyView.findViewById(caret).setOnClickListener(collapseListener);

        ordersScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d(TAG, "onScrollChange: " + scrollX);
            }
        });

        // Additional behaviour on bottom sheet scroll
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            private int prevState = STATE_COLLAPSED;
            private float prevOffset = 0;

            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == STATE_EXPANDED) {

                    prevState = STATE_EXPANDED;

                    if (sessionManager.existingRequestedOrders()) {
                        if (payButton.getVisibility() == View.GONE) {
                            revealPayFAB();
                        }
                    }
                    doExpandTransition();

                } else if (newState == STATE_COLLAPSED) {

                    prevState = STATE_COLLAPSED;

                    doCollapseTransition();
                    payButton.setVisibility(View.GONE);

                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                if (slideOffset < 0) return; //ignore hiding offsets
                bottomLayout.findViewById(caret).setRotation(slideOffset * 180);
                modalBg.setAlpha(slideOffset * .9F);
                payButton.setAlpha(slideOffset * 2 - 1);

                requestedOrdersWrapper.setTranslationY((1 - slideOffset) * 2 * bottomSheetHeight);
                if (prevOffset > slideOffset && prevState == STATE_EXPANDED && slideOffset < .4) {
                    doCollapseTransition();
                    prevState = STATE_COLLAPSED;
                } else if (prevOffset < slideOffset && prevState == STATE_COLLAPSED && slideOffset > .2) {
                    doExpandTransition();
                    prevState = STATE_EXPANDED;
                }
                prevOffset = slideOffset;

            }
        });

        View.OnClickListener collapsedOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(STATE_EXPANDED);
//                doExpandTransition(session.hasPendingOrders());
            }
        };
        collapsedOrdersLayout.setOnClickListener(collapsedOnClickListener);
        collapsedOrdersEmptyLayout.setOnClickListener(collapsedOnClickListener);

        View.OnClickListener orderButtonClickedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardOrdersButtonTooltip();
                sessionManager.placePendingOrders();
            }
        };
        orderButton.setOnClickListener(orderButtonClickedListener);
        orderButtonExpanded.setOnClickListener(orderButtonClickedListener);

        return view;
    }

    private void doExpandTransition() {

        if (expanded) return;
        expanded = true;

        Log.d(TAG, "doExpandTransition: ");

        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(200);
        autoTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                bottomLayout.findViewById(R.id.caret).setRotation(180);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        TransitionManager.beginDelayedTransition(pendingOrdersWrapper, autoTransition);
        pendingOrdersWrapper.removeAllViews();
        pendingOrdersWrapper.addView(pendingExpandedViewRef);
        requestedOrdersWrapper.setAlpha(1);
    }

    private void doCollapseTransition() {

        if (!expanded) return;
        expanded = false;

        Log.d(TAG, "doCollapseTransition: ");

        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(250);
        autoTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                bottomLayout.findViewById(R.id.caret).setRotation(0);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

        TransitionManager.beginDelayedTransition(pendingOrdersWrapper, autoTransition);
        pendingOrdersWrapper.removeAllViews();
        pendingOrdersWrapper.addView(collapsedViewRef);
        requestedOrdersWrapper.setAlpha(0);
    }

    Map<Item, ItemListPendingOrderCollapsedBinding> pendingOrderCollapsedBindingMap = new HashMap<>();

    /**
     * Update view based on new pending orders, ready to be ordered
     *
     * @param order
     */
    @Override
    public void onNewPendingOrderAdded(Order order, boolean isOrderNew) {

        if (clearJustOrderedStateTimer != null)
            clearJustOrderedStateTimer.cancel();

        setStateCollapsed();

        collapsedViewRef = collapsedOrdersLayout;

        pendingOrdersWrapper.removeAllViews();
        pendingOrdersWrapper.addView(collapsedOrdersLayout);
        bottomLayout.findViewById(R.id.caret).setRotation(180);

        pendingExpandedViewRef = pendingExpandedWithItemsView;

        final LayoutInflater inflater = LayoutInflater.from(getActivity());

        pendingItemsLayoutCollapsed.setVisibility(View.VISIBLE);
        pendingOrdersCollapsedTitle.setText(R.string.pending_products_title);
        pendingImageCollapsed.setVisibility(View.INVISIBLE);
        orderButton.setVisibility(View.VISIBLE);

        final ItemListPendingOrderCollapsedBinding collapsedBinding;

        // pending order didn't exist for this item
        if (isOrderNew) {

            /* Create new collapsed view */
            collapsedBinding = ItemListPendingOrderCollapsedBinding.inflate(inflater);
            collapsedBinding.setOrder(order);
            pendingOrderCollapsedBindingMap.put(order.item, collapsedBinding);
            pendingItemsLayoutCollapsed.addView(collapsedBinding.getRoot());

            /* Add extended view */
            final ItemListPendingOrderBinding expandedBinding = ItemListPendingOrderBinding.inflate(inflater);
            expandedBinding.setOrder(order);
            expandedBinding.setHandlers(new PendingOrderItemClickHandler() {
                @Override
                public void onOrderClick(View view, Order order) {
                }

                @Override
                public void onOrderRemovedClick(View view, Order order) {
                    decreaseOrderCount(order, expandedBinding.getRoot(), collapsedBinding.getRoot());
                }
            });
            pendingItemsLayoutExpanded.addView(expandedBinding.getRoot());

        } else {
            collapsedBinding = pendingOrderCollapsedBindingMap.get(order.item);
        }

        /*
         * Take the item icon and set the ripple effect pressed, then disable with a handler
         */
        final RippleDrawable background = (RippleDrawable) collapsedBinding.getRoot().getBackground();
        background.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                background.setState(new int[]{});
            }
        }, 2000);

        String ordersButtonString = getString(R.string.order_num_items, sessionManager.getPendingItemsCount());
        orderButton.setText(ordersButtonString);
        orderButtonExpanded.setText(ordersButtonString);


        /* Animate collapsed icon */
//        final ViewGroup iconView = (ViewGroup) collapsedBinding.getRoot();
//        ValueAnimator alphaAnimation = ValueAnimator.ofInt(255, 0);
//        alphaAnimation.setStartDelay(200);
//        alphaAnimation.setDuration(400);
//        alphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                iconView.getBackground().setAlpha((Integer) animation.getAnimatedValue());
//            }
//        });
//        alphaAnimation.start();

            /* Animate new icon */
//            ValueAnimator bgAnimator = ValueAnimator.ofArgb(getResources().getColor(R.color.colorAccentAlt), Color.TRANSPARENT);
//            bgAnimator.setDuration(800);
//            bgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    iconView.setBackgroundColor((Integer) animation.getAnimatedValue());
//                }
//            });
//            bgAnimator.start();
//
//            int iconSide = (int) getResources().getDimension(R.dimen.small_icon);
//            int finalRadius = (int) (iconSide / 2 * 1.44);
//            Animator anim = ViewAnimationUtils.createCircularReveal(collapsedBinding.getRoot(), iconSide / 2, iconSide / 2, 0, finalRadius);
//            anim.start();


        /* Make bottom layout bounce */
        Animator bounce = AnimatorInflater.loadAnimator(getActivity(), R.animator.orders_bounce_animator);
        bounce.setTarget(bottomLayout);
        bounce.start();

        displayOrdersButtonTooltip();

    }

    public void displayOrdersButtonTooltip() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Tooltip.make(getActivity(),
                        new Tooltip.Builder(ORDERS_BUTTON_TOOLTIP_ID)
                                .withStyleId(R.style.Tooltip)
                                .maxWidth(getResources().getDimensionPixelSize(R.dimen.tooltip_width))
                                .anchor(orderButton, Tooltip.Gravity.LEFT)
                                .closePolicy(new Tooltip.ClosePolicy()
                                        .insidePolicy(true, false)
                                        .outsidePolicy(false, false), 5000)
                                .text(getString(R.string.orders_button_tooltip))
                                .withArrow(true)
                                .withOverlay(false)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                ).show();
            }
        }, 100);

    }

    public void discardOrdersButtonTooltip() {
        Tooltip.remove(getActivity(), ORDERS_BUTTON_TOOLTIP_ID);
    }

    /**
     * Decrease by one the amount required for one item. If zero delete the order
     *
     * @param order
     * @param expandedBindingRoot
     * @param collapsedBindingRoot
     */
    private void decreaseOrderCount(Order order, View expandedBindingRoot, View collapsedBindingRoot) {

        boolean orderedRemoved = sessionManager.removeItemInPendingOrder(order);

        if (orderedRemoved) {

            TransitionManager.beginDelayedTransition(bottomLayout);
            pendingItemsLayoutExpanded.removeView(expandedBindingRoot);
            pendingItemsLayoutCollapsed.removeView(collapsedBindingRoot);

            if (!sessionManager.hasPendingOrders()) {
                setEmptyPendingOrdersState();
            }

        }
    }


    /**
     * Update the view based on the newly ordered items
     */
    @Override
    public void onOrdersPlaced(List<Order> newlyPlacedOrders) {

        pendingOrderCollapsedBindingMap.clear();

        orderButton.animate().alpha(0).start();

        int delay = 0;
        for (int i = 0; i < pendingItemsLayoutExpanded.getChildCount(); i++) {
            View view = pendingItemsLayoutExpanded.getChildAt(i);
            view.animate().translationXBy(pendingItemsLayoutExpanded.getWidth()).setStartDelay(delay).setDuration(300).start();
            delay += 100;
        }
        delay = 0;
        for (int i = pendingItemsLayoutCollapsed.getChildCount() - 1; i >= 0; i--) {
            View view = pendingItemsLayoutCollapsed.getChildAt(i);
            view.animate().translationXBy(pendingItemsLayoutCollapsed.getWidth()).alpha(0).setStartDelay(delay).setDuration(300).start();
            delay += 100;
        }

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        final List<View> newlyAddedViews = new ArrayList<>();

        Transition transition = new AutoTransition();

        final int finalDelay = delay + 300;
        transition.setStartDelay(finalDelay);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                for (final View view : newlyAddedViews) {
                    ValueAnimator bgAnimator = ValueAnimator.ofArgb(getResources().getColor(R.color.translucent_grey), Color.TRANSPARENT);
                    bgAnimator.setStartDelay(finalDelay + 400);
                    bgAnimator.setDuration(1400);
                    bgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimator animation) {
                            view.setBackgroundColor((Integer) animation.getAnimatedValue());
                        }
                    });
                    bgAnimator.start();
                }
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                orderButton.setAlpha(1);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

        TransitionManager.beginDelayedTransition(isExpanded() ? bottomLayout : pendingOrdersWrapper, transition);

        for (Order order : newlyPlacedOrders) {
            ItemListRequestedOrderBinding itemListRequestedOrderBinding = ItemListRequestedOrderBinding.inflate(inflater);
            itemListRequestedOrderBinding.setOrder(order);
            View view = itemListRequestedOrderBinding.getRoot();
            newlyAddedViews.add(view);
            requestedItemsLayout.addView(view, 0);
        }

        requestedOrdersWrapper.removeAllViews();
        requestedOrdersWrapper.addView(requestedOrdersView);

        float sum = sessionManager.getRequestedItemsPrice();
        totalPriceTextView.setText(String.format(Locale.getDefault(), "%.2f %s", sum, sessionManager.getCurrency()));

        setJustOrderedState();

        if (isExpanded()) {
            revealPayFAB();
        }
    }

    private void revealPayFAB() {
        payButton.setVisibility(View.VISIBLE);
        ViewAnimationUtils.createCircularReveal(payButton, payButton.getWidth() / 2, payButton.getWidth() / 2, 0, payButton.getWidth()).start();
    }

    private void setEmptyPendingOrdersState() {


        if (isExpanded()) {
            pendingOrdersWrapper.removeAllViews();
            pendingOrdersWrapper.addView(pendingExpandedEmptyView);
            bottomLayout.findViewById(R.id.caret).setRotation(180);
        } else {
            pendingOrdersWrapper.removeAllViews();
            pendingOrdersWrapper.addView(collapsedOrdersEmptyLayout);
        }

        collapsedViewRef = collapsedOrdersEmptyLayout;
        pendingExpandedViewRef = pendingExpandedEmptyView;

        pendingItemsLayoutExpanded.removeAllViews();
        pendingItemsLayoutCollapsed.removeAllViews();

    }

    private void setJustOrderedState() {

        if (isExpanded()) {
            pendingOrdersWrapper.removeAllViews();
            pendingOrdersWrapper.addView(pendingExpandedJustOrderedView);
            bottomLayout.findViewById(R.id.caret).setRotation(180);
        } else {
            pendingOrdersWrapper.removeAllViews();
            pendingOrdersWrapper.addView(collapsedOrdersEmptyLayout);
        }

        collapsedViewRef = collapsedOrdersEmptyLayout;
        pendingExpandedViewRef = pendingExpandedJustOrderedView;

        pendingItemsLayoutExpanded.removeAllViews();
        pendingItemsLayoutCollapsed.removeAllViews();

        pendingOrdersCollapsedSubtitle.setText(R.string.on_the_way_long);
        pendingOrdersCollapsedTitle.setText(R.string.on_the_way);
        pendingImageCollapsed.setVisibility(View.VISIBLE);
        pendingImageCollapsed.setImageResource(R.drawable.ic_check_circle_24dp);
        orderButton.setVisibility(View.INVISIBLE);

//        clearJustOrderedStateTimer = new CountDownTimer(5000, 5000) {
//
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//                if (isAdded()) {
//                    TransitionManager.beginDelayedTransition(pendingOrdersWrapper);
//                    setEmptyPendingOrdersState();
//                }
//            }
//        };
//        clearJustOrderedStateTimer.start();

    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setBottomSheetCollapsedIfHasOrders() {

        if (!sessionManager.hasSessionStarted() || !sessionManager.hasAnyOrder()) {
            setStateHidden();
            return;
        }
        setStateCollapsed();

    }

    public void setStateCollapsed() {
        if (bottomSheetBehavior.getState() == STATE_COLLAPSED) return;
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(STATE_COLLAPSED);
        doCollapseTransition();
    }

    public void setStateHidden() {
        if (bottomSheetBehavior.getState() == STATE_HIDDEN) return;
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(STATE_HIDDEN);
        doCollapseTransition();
    }


    public void hideOrdersButton() {
        orderButton.getBackground().setAlpha(0);
        setStateHidden();
    }

    public void displayOrdersButton() {
        ValueAnimator alphaAnimation = ValueAnimator.ofInt(0, 255);
        alphaAnimation.setDuration(400);
        alphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                orderButton.getBackground().setAlpha((Integer) animation.getAnimatedValue());
            }
        });
        alphaAnimation.start();
        setBottomSheetCollapsedIfHasOrders();
    }
}
