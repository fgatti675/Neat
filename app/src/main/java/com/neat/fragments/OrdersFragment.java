package com.neat.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

public class OrdersFragment extends Fragment implements SessionManager.OnOrdersPlacedListener {

    public static final String FRAGMENT_TAG = "ORDERS_FRAGMENT";

    private static final String TAG = OrdersFragment.class.getSimpleName();

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

    @Bind(R.id.caret)
    View caret;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.modal_bg)
    View modalBg;

    @Bind(R.id.orders_layout)
    ViewGroup collapsedOrdersLayout;

    @Bind(R.id.collapsed_orders_title)
    TextView pendingOrdersCollapsedTitle;

    @Bind(R.id.collapsed_orders_subtitle)
    TextView pendingOrdersCollapsedSubtitle;

    @Bind(R.id.collapsed_orders_image)
    ImageView pendingImageCollapsed;

    @Bind(R.id.pending_items_layout)
    ViewGroup pendingItemsLayoutCollapsed;

    @Bind(R.id.requested_orders_empty_view)
    ViewGroup requestedItemsEmptyView;

    ViewGroup requestedItemsLayout;

    @Bind(R.id.order_button)
    Button orderButton;

    @Bind(R.id.pay_button)
    FloatingActionButton payButton;

    // this is a reference to the view that gets displayed when the orders view is expanded, and can be one the 3 below
    ViewGroup pendingExpandedView;

    // the 3 views that can be displayed when the layout is expanded
    ViewGroup pendingExpandedWithItemsView;
    ViewGroup pendingExpandedEmptyView;
    ViewGroup pendingExpandedJustOrderedView;

    ViewGroup pendingItemsLayoutExpanded;

    Button orderButtonExpanded;
    ViewGroup requestedOrdersView;

    TextView totalPriceTextView;

    boolean expanded = false;

    private BottomSheetBehavior bottomSheetBehavior;

    private float bottomSheetHeight;

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
    }

    @Override
    public void onStop() {
        super.onStop();
        sessionManager.removeOnOrdersPlacedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, view);

        bottomSheetHeight = getResources().getDimension(R.dimen.orders_layout_sneak_peak);

        bottomSheetBehavior = BottomSheetBehavior.from(ordersScrollView);

        // create and bind expanded views ans scene
        pendingExpandedWithItemsView = (ViewGroup) inflater.inflate(R.layout.layout_pending_orders_expanded, pendingOrdersWrapper, false);
        pendingItemsLayoutExpanded = (ViewGroup) pendingExpandedWithItemsView.findViewById(R.id.pending_items_layout);
        orderButtonExpanded = (Button) pendingExpandedWithItemsView.findViewById(R.id.order_button);

        // initially set the expanded view reference as empty
        pendingExpandedView = pendingExpandedEmptyView;

        // create expanded empty view and scene
        pendingExpandedEmptyView = (ViewGroup) inflater.inflate(R.layout.layout_pending_orders_expanded_empty, pendingOrdersWrapper, false);
        pendingExpandedJustOrderedView = (ViewGroup) inflater.inflate(R.layout.layout_pending_orders_expanded_ordered, pendingOrdersWrapper, false);

        requestedOrdersView = (ViewGroup) inflater.inflate(R.layout.layout_requested_orders, requestedOrdersWrapper, false);
        requestedItemsLayout = (ViewGroup) requestedOrdersView.findViewById(R.id.requested_items_layout);
        totalPriceTextView = (TextView) requestedOrdersView.findViewById(R.id.total_amount_view);

        // Collapse on caret click
        View.OnClickListener collapseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);
            }
        };
        pendingExpandedWithItemsView.findViewById(R.id.caret).setOnClickListener(collapseListener);
        pendingExpandedEmptyView.findViewById(R.id.caret).setOnClickListener(collapseListener);

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
                caret.setRotation(slideOffset * 180);
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

        collapsedOrdersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(STATE_EXPANDED);
//                doExpandTransition(session.hasPendingOrders());
            }
        });

        View.OnClickListener ordersPlacedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.placePendingOrders();
            }
        };
        orderButton.setOnClickListener(ordersPlacedListener);
        orderButtonExpanded.setOnClickListener(ordersPlacedListener);

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
                caret.setRotation(180);
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
        pendingOrdersWrapper.addView(pendingExpandedView);
        requestedOrdersWrapper.setAlpha(1);
        caret = bottomLayout.findViewById(R.id.caret);
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
                caret.setRotation(0);
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
        pendingOrdersWrapper.addView(collapsedOrdersLayout);
        requestedOrdersWrapper.setAlpha(0);
        caret = pendingOrdersWrapper.findViewById(R.id.caret);
    }

    public void addPendingItem(Item item) {

        setStateCollapsed();

        pendingExpandedView = pendingExpandedWithItemsView;

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        pendingOrdersCollapsedSubtitle.setVisibility(View.INVISIBLE);
        pendingItemsLayoutCollapsed.setVisibility(View.VISIBLE);
        pendingOrdersCollapsedTitle.setText(R.string.pending_products_title);
        pendingImageCollapsed.setVisibility(View.INVISIBLE);
        orderButton.setVisibility(View.VISIBLE);

        Order order = sessionManager.requestItem(item);

        // pending order didn't exist for this item
        if (order.count == 1) {

            /* Create new collapsed icon */
            final ItemListPendingOrderCollapsedBinding collapsedBinding = ItemListPendingOrderCollapsedBinding.inflate(inflater);
            collapsedBinding.setOrder(order);
            pendingItemsLayoutCollapsed.addView(collapsedBinding.getRoot());

            /* Animate new icon */
            int iconSide = (int) getResources().getDimension(R.dimen.small_icon);
            int finalRadius = (int) (iconSide / 2 * 1.44);
            Animator anim = ViewAnimationUtils.createCircularReveal(collapsedBinding.getRoot(), iconSide / 2, iconSide / 2, 0, finalRadius);
            anim.start();

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
        }

        /* Make bottom layout bounce */
        Animator bounce = AnimatorInflater.loadAnimator(getActivity(), R.animator.orders_bounce_animator);
        bounce.setTarget(bottomLayout);
        bounce.start();

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

            if (sessionManager.hasPendingOrders()) {
                setEmptyPendingOrdersState();
            }

        }
    }


    /**
     * Update the view based on the newly ordered items
     */
    @Override
    public void onOrdersPlaced(List<Order> newlyPlacedOrders) {

        int delay = 0;
        for (int i = 0; i < pendingItemsLayoutExpanded.getChildCount(); i++) {
            View view = pendingItemsLayoutExpanded.getChildAt(i);
            view.animate().translationXBy(pendingItemsLayoutExpanded.getWidth()).setStartDelay(delay).setDuration(300).start();
            delay += 100;
        }
        orderButton.animate().alpha(0).start();
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

        TransitionManager.beginDelayedTransition(bottomLayout, transition);

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
    }

    private void onOrdersDelivered() {

    }

    private void revealPayFAB() {
        payButton.setVisibility(View.VISIBLE);
        ViewAnimationUtils.createCircularReveal(payButton, payButton.getWidth() / 2, payButton.getWidth() / 2, 0, payButton.getWidth()).start();
    }

    private void setEmptyPendingOrdersState() {

        if (!isCollapsed()) {
            pendingOrdersWrapper.removeAllViews();
            pendingOrdersWrapper.addView(pendingExpandedEmptyView);
            caret = bottomLayout.findViewById(R.id.caret);
            caret.setRotation(180);
        }

        pendingExpandedView = pendingExpandedEmptyView;

        pendingItemsLayoutExpanded.removeAllViews();
        pendingItemsLayoutCollapsed.removeAllViews();

        pendingOrdersCollapsedSubtitle.setVisibility(View.VISIBLE);
        pendingOrdersCollapsedSubtitle.setText(R.string.pending_products_subtitle);
        pendingOrdersCollapsedTitle.setText(R.string.pending_products_empty);
        pendingImageCollapsed.setVisibility(View.VISIBLE);
        pendingImageCollapsed.setImageResource(R.drawable.ic_waiter_color);
        orderButton.setVisibility(View.INVISIBLE);

    }

    private void setJustOrderedState() {

        if (!isCollapsed()) {
            pendingOrdersWrapper.removeAllViews();
            pendingOrdersWrapper.addView(pendingExpandedJustOrderedView);
            caret = bottomLayout.findViewById(R.id.caret);
            caret.setRotation(180);
            revealPayFAB();
        }

        pendingExpandedView = pendingExpandedJustOrderedView;

        pendingItemsLayoutExpanded.removeAllViews();
        pendingItemsLayoutCollapsed.removeAllViews();

        pendingOrdersCollapsedSubtitle.setVisibility(View.VISIBLE);
        pendingOrdersCollapsedSubtitle.setText(R.string.on_the_way_long);
        pendingOrdersCollapsedTitle.setText(R.string.on_the_way);
        pendingImageCollapsed.setVisibility(View.VISIBLE);
        pendingImageCollapsed.setImageResource(R.drawable.ic_check_circle_24dp);
        orderButton.setVisibility(View.INVISIBLE);

    }

    public boolean isExpanded() {
        return expanded;
    }

    private boolean isCollapsed() {
        return bottomSheetBehavior.getState() == STATE_COLLAPSED;
    }

    public void setBottomSheetDisplayedIfHasOrders(boolean displayed) {

        if (!sessionManager.hasAnyOrder()) {
            setStateHidden();
            return;
        }

        if (!displayed) {
            setStateHidden();
        } else {
            setStateCollapsed();
        }

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


}
