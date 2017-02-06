package com.neat.view.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
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
import com.neat.dagger.SessionScope;
import com.neat.databinding.ItemListPendingOrderBinding;
import com.neat.databinding.ItemListPendingOrderCollapsedBinding;
import com.neat.databinding.ItemListRequestedOrderBinding;
import com.neat.model.classes.Order;
import com.neat.viewmodel.OrderViewModel;
import com.neat.viewmodel.PendingOrderItemClickHandler;
import com.neat.viewmodel.SessionViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.sephiroth.android.library.tooltip.Tooltip;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static com.neat.R.id.caret;

@SessionScope
public class OrdersFragment extends Fragment implements SessionViewModel.OnOrdersPlacedListener, SessionViewModel.OnPendingOrdersChangedListener {

    private static final String TAG = OrdersFragment.class.getSimpleName();
    public static final int ORDERS_BUTTON_TOOLTIP_ID = 101;

    @Inject
    SessionViewModel sessionViewModel;

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
    private Handler handler = new Handler();

    private Map<OrderViewModel, ItemListPendingOrderCollapsedBinding> pendingOrderCollapsedBindingMap = new HashMap<>();
    private Map<OrderViewModel, ItemListPendingOrderBinding> pendingOrderExpandedBindingMap = new HashMap<>();

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NeatApplication.getComponent(getActivity()).application().getSessionComponent().inject(this);
        sessionViewModel.addOnOrdersPlacedListener(this);
        sessionViewModel.addOnNewPendingOrderAddedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sessionViewModel.removeOnOrdersPlacedListener(this);
        sessionViewModel.removeOnNewPendingOrderAddedListener(this);
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

                    if (sessionViewModel.existingRequestedOrders()) {
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
                sessionViewModel.placePendingOrders();
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


    /**
     * Update view based on new pending orders, ready to be ordered
     *
     * @param orderViewModel
     */
    @Override
    public void onPendingOrderCreated(final OrderViewModel orderViewModel) {

        final boolean bottomStatusChanged = setStateCollapsed();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                setStateWithPendingOrders();

                final LayoutInflater inflater = LayoutInflater.from(getActivity());

                /* Create new collapsed view */
                ItemListPendingOrderCollapsedBinding collapsedBinding = ItemListPendingOrderCollapsedBinding.inflate(inflater);
                collapsedBinding.setOrderviewmodel(orderViewModel);
                pendingOrderCollapsedBindingMap.put(orderViewModel, collapsedBinding);
                pendingItemsLayoutCollapsed.addView(collapsedBinding.getRoot());
                setIconRippleActivated(collapsedBinding.getRoot());

                /* Add extended view */
                ItemListPendingOrderBinding expandedBinding = ItemListPendingOrderBinding.inflate(inflater);
                expandedBinding.setOrderviewmodel(orderViewModel);
                pendingOrderExpandedBindingMap.put(orderViewModel, expandedBinding);
                pendingItemsLayoutExpanded.addView(expandedBinding.getRoot());

                if (!bottomStatusChanged) {
                    animateBottomBounce();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayOrdersButtonTooltip();
                    }
                }, 48);
            }

        }, 128);

    }


    @Override
    public void onPendingOrderUpdated(final OrderViewModel orderViewModel) {

        final boolean bottomStatusChanged = setStateCollapsed();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                setStateWithPendingOrders();

                final ItemListPendingOrderCollapsedBinding collapsedBinding;

                collapsedBinding = pendingOrderCollapsedBindingMap.get(orderViewModel);

                setIconRippleActivated(collapsedBinding.getRoot());

                if (!bottomStatusChanged) {
                    /* Make bottom layout bounce */
                    animateBottomBounce();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayOrdersButtonTooltip();
                    }
                }, 48);
            }


        }, 128);
    }

    @Override
    public void onPendingOrderRemoved(OrderViewModel orderViewModel) {
        TransitionManager.beginDelayedTransition(bottomLayout);
        pendingItemsLayoutExpanded.removeView(expandedBindingRoot);
        pendingItemsLayoutCollapsed.removeView(collapsedBindingRoot);

        if (!sessionViewModel.hasPendingOrders()) {
            setEmptyPendingOrdersState();
        }
    }


    private void setStateWithPendingOrders() {
        collapsedViewRef = collapsedOrdersLayout;

        pendingOrdersWrapper.removeAllViews();
        pendingOrdersWrapper.addView(collapsedOrdersLayout);
        bottomLayout.findViewById(R.id.caret).setRotation(180);

        pendingExpandedViewRef = pendingExpandedWithItemsView;

        pendingItemsLayoutCollapsed.setVisibility(View.VISIBLE);
        pendingOrdersCollapsedTitle.setText(R.string.pending_products_title);
        pendingImageCollapsed.setVisibility(View.INVISIBLE);
        orderButton.setVisibility(View.VISIBLE);
    }

    /**
     * Take the item icon and set the ripple effect pressed, then disable with a handler
     *
     * @param view must have a RippleDrawable as background
     */
    private void setIconRippleActivated(final View view) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final RippleDrawable background = (RippleDrawable) view.getBackground();
                background.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        background.setState(new int[]{});
                    }
                }, 2000);

                String ordersButtonString = getString(R.string.order_num_items, sessionViewModel.getPendingItemsCount());
                orderButton.setText(ordersButtonString);
                orderButtonExpanded.setText(ordersButtonString);
            }
        }, 16);
    }

    public void displayOrdersButtonTooltip() {
        Tooltip.make(getActivity(),
                new Tooltip.Builder(ORDERS_BUTTON_TOOLTIP_ID)
                        .fadeDuration(200)
                        .withStyleId(R.style.Tooltip)
                        .maxWidth(getResources().getDimensionPixelSize(R.dimen.tooltip_width))
                        .anchor(orderButton, Tooltip.Gravity.TOP)
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
    private void decreaseOrderCount(OrderViewModel order, View expandedBindingRoot, View collapsedBindingRoot) {

        boolean orderedRemoved = sessionViewModel.removeItemInPendingOrder(order);

        if (orderedRemoved) {



        }
    }

    /**
     * Make bottom layout bounce
     */
    private void animateBottomBounce() {
        Animator bounce = AnimatorInflater.loadAnimator(getActivity(), R.animator.orders_bounce_animator);
        bounce.setStartDelay(32);
        bounce.setTarget(bottomLayout);
        bounce.start();
    }


    /**
     * Update the view based on the newly ordered items
     */
    @Override
    public void onOrdersPlaced(List<OrderViewModel> newlyPlacedOrders) {

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

        for (OrderViewModel order : newlyPlacedOrders) {
            ItemListRequestedOrderBinding itemListRequestedOrderBinding = ItemListRequestedOrderBinding.inflate(inflater);
            itemListRequestedOrderBinding.setOrder(order);
            View view = itemListRequestedOrderBinding.getRoot();
            newlyAddedViews.add(view);
            requestedItemsLayout.addView(view, 0);
        }

        requestedOrdersWrapper.removeAllViews();
        requestedOrdersWrapper.addView(requestedOrdersView);

        totalPriceTextView.setText(sessionViewModel.getTotalPriceString());

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

    public void setStateCollapsedIfHasOrders() {

        if (!sessionViewModel.hasSessionStarted() || !sessionViewModel.hasAnyOrder()) {
            setStateHidden();
            return;
        }
        setStateCollapsed();

    }

    /**
     * @return true if state changed
     */
    public boolean setStateCollapsed() {
        if (bottomSheetBehavior.getState() == STATE_COLLAPSED) return false;
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(STATE_COLLAPSED);
        doCollapseTransition();
        return true;
    }

    /**
     * @return true if state changed
     */
    public boolean setStateHidden() {
        if (bottomSheetBehavior.getState() == STATE_HIDDEN) return false;
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(STATE_HIDDEN);
        doCollapseTransition();
        return true;
    }

}
