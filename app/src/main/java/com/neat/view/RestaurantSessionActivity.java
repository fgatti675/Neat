package com.neat.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.neat.NeatApplication;
import com.neat.PaymentActivity;
import com.neat.R;
import com.neat.databinding.ActivityRestaurantSessionSelectionBinding;
import com.neat.model.RestaurantProvider;
import com.neat.model.SessionManager;
import com.neat.model.classes.MenuSection;
import com.neat.model.classes.Order;
import com.neat.model.classes.Restaurant;
import com.neat.model.classes.Session;
import com.neat.model.classes.User;
import com.neat.view.fragments.ItemFeaturedFragment;
import com.neat.view.fragments.ItemListFragment;
import com.neat.view.fragments.ItemListSmallFragment;
import com.neat.view.fragments.OrdersFragment;
import com.neat.viewmodel.RestaurantActivityViewModel;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RestaurantSessionActivity extends AppCompatActivity
        implements RestaurantProvider.Callback,
        SessionManager.OnSessionJoinedCallbacks,
        SessionManager.OnOrdersPlacedListener {

    private static final String TAG = RestaurantSessionActivity.class.getSimpleName();

    @Inject
    RestaurantProvider restaurantProvider;

    @Inject
    SessionManager sessionManager;

    @Inject
    @Named("logged_user")
    User user;

    RestaurantActivityViewModel restaurantActivityViewModel;

    private OrdersFragment ordersFragment;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.main_layout)
    ViewGroup mainLayout;

    @Bind(R.id.main_sections_container)
    ViewGroup mainSectionsContainer;

    @Bind(R.id.table_participants)
    TextView tableParticipantsView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.header)
    ImageView header;

//    @Bind(R.id.pay_button)
//    Button payButton;

    @Bind(R.id.subtitle)
    TextView subtitle;

    @Bind(R.id.table_details)
    ViewGroup tableDetailsLayout;

    @Bind(R.id.header_caret)
    View headerCaret;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.app_bar)
    AppBarLayout appBarLayout;

    private Restaurant restaurant;

    private View payAction;
    private View menuAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        NeatApplication.getComponent(this).application().createSessionComponent().inject(this);

        if (user == null) { // don't even bother
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_restaurant_session_selection);

        ButterKnife.bind(this);

        sessionManager.addOnSessionJoinedCallbacks(this);
        sessionManager.addOnOrdersPlacedListener(this);

        ordersFragment = (OrdersFragment) getFragmentManager().findFragmentById(R.id.orders_fragment);

        toolbar.inflateMenu(R.menu.menu_restaurant);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_menu) {
                    return true;
                } else if (id == R.id.action_disconnect) {
                    logout();
                    return true;
                }
                return false;
            }
        });

        payAction = toolbar.findViewById(R.id.action_pay);
        payAction.setVisibility(View.GONE);
        payAction.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewAnimationUtils.createCircularReveal(
                        payAction,
                        payAction.getMeasuredWidth() / 2,
                        payAction.getMeasuredHeight() / 2, 0,
                        Math.max(payAction.getWidth(), payAction.getHeight()) / 2)
                        .start();
                payAction.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        payAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantSessionActivity.this, PaymentActivity.class));
            }
        });
        menuAction = toolbar.findViewById(R.id.action_menu);

//        final float offsetX = getResources().getDimension(R.dimen.pay_button_toolbar_offset_x_collapsed);
//        final float offsetY = getResources().getDimension(R.dimen.pay_button_toolbar_offset_y_expanded);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            float tablesViewHeight = getResources().getDimension(R.dimen.table_selection_height);

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float scrollPercentage = 1 + ((float) verticalOffset) / (float) appBarLayout.getTotalScrollRange();
//                float translationX = -(1 - scrollPercentage) * offsetX;
//                float translationY = scrollPercentage * -offsetY;
//                payButton.setTranslationX(translationX);
//                payButton.setTranslationY(translationY);
//                payButton.setAlpha(scrollPercentage);
                payAction.setAlpha(1 - scrollPercentage);
                menuAction.setAlpha(1 - scrollPercentage);

                // fade out elements that disappear
                float normalizedPercentage = (scrollPercentage - 0.5F) * 2;
                subtitle.setAlpha(normalizedPercentage);
                headerCaret.setAlpha(normalizedPercentage);

                collapsingToolbarLayout.setPadding(0, 0, 0, (int) (tablesViewHeight * normalizedPercentage));

                if (scrollPercentage < .1) return;

                // display orders layout based on scroll
                if (scrollPercentage < .5)
                    ordersFragment.setStateCollapsedIfHasOrders();
                else
                    ordersFragment.setStateHidden();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionManager.removeOnOrdersPlacedListener(this);
        sessionManager.removeOnSessionJoinedCallbacks(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (restaurant == null)
            restaurantProvider.getRestaurant("lateral", this);
    }


    @Override
    public void onRestaurantLoaded(final Restaurant restaurant) {

        this.restaurant = restaurant;

        sessionManager.joinSession(restaurant, restaurant.tables.values().iterator().next().id);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            // get the center for the clipping circle
//            int cx = payButton.getMeasuredWidth() / 2;
//            int cy = payButton.getMeasuredHeight() / 2;
//
//            // get the final radius for the clipping circle
//            int finalRadius = Math.max(payButton.getWidth(), payButton.getHeight()) / 2;
//
//            // create the animator for this view (the start radius is zero)
//            Animator anim = ViewAnimationUtils.createCircularReveal(payButton, cx, cy, 0, finalRadius);
//
//            // make the view visible and start the animation
//            payButton.setVisibility(View.VISIBLE);
//            anim.start();
//        }
    }


    @Override
    public void onSessionJoined(final Session session) {

        Log.d(TAG, "onSessionJoined: ");
        restaurantActivityViewModel = new RestaurantActivityViewModel(RestaurantSessionActivity.this, session);

        ActivityRestaurantSessionSelectionBinding binding = ActivityRestaurantSessionSelectionBinding.bind(mainLayout);
        binding.setViewModel(restaurantActivityViewModel);

        ordersFragment.setStateHidden();

        Glide.with(RestaurantSessionActivity.this).load(restaurant.headerUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(header);

        collapsingToolbarLayout.setTitle(restaurant.title);
        subtitle.setText(restaurant.subtitle);

        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (MenuSection section : restaurant.menu.sections) {
            if (section.type != null) {
                switch (section.type) {
                    case small:
                        transaction.add(R.id.main_sections_container, ItemListSmallFragment.newInstance(section), null);
                        break;
                    case featured:
                        transaction.add(R.id.main_sections_container, ItemFeaturedFragment.newInstance(section), null);
                        break;
                    case list:
                    default:
                        transaction.add(R.id.main_sections_container, ItemListFragment.newInstance(section), null);
                        break;

                }
            }
            // default to list
            else {
                transaction.add(R.id.main_sections_container, ItemListFragment.newInstance(section), null);
            }

        }
        transaction.commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {
        if (ordersFragment.isExpanded())
            ordersFragment.setStateCollapsed();
        else
            super.onBackPressed();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void logout() {
        // TODO: abstract away
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goToLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ItemDetailsActivity.REQUEST_ITEM_DETAILS) {
            if (resultCode == ItemDetailsActivity.RESULT_ITEM_ADDED) {
//                sessionViewModel.addPendingOrder(
//                        (Item) data.getSerializableExtra(ItemDetailsActivity.EXTRA_ITEM),
//                        data.getIntExtra(ItemDetailsActivity.EXTRA_ITEM_COUNT, -1));
            }
        }

    }

    @Override
    public void onOrdersPlaced(Set<Order> newlyPlacedOrders) {
        /*
         * Reveal pay action
         */
        if (payAction.getVisibility() != View.VISIBLE) {
            payAction.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onOrdersPlacedError() {
        Toast.makeText(this, R.string.error_placing_orders, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onSessionJoinFail() {
        // TODO
    }
}
