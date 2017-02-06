package com.neat.view;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.neat.NeatApplication;
import com.neat.R;
import com.neat.model.RestaurantProvider;
import com.neat.model.SessionManager;
import com.neat.view.fragments.ItemFeaturedFragment;
import com.neat.view.fragments.ItemListFragment;
import com.neat.view.fragments.ItemListSmallFragment;
import com.neat.view.fragments.OrdersFragment;
import com.neat.model.classes.Item;
import com.neat.model.classes.MenuSection;
import com.neat.model.classes.Order;
import com.neat.model.classes.Restaurant;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity
        implements RestaurantProvider.Callback,
        SessionManager.OnOrdersPlacedListener {

    private static final String TAG = MenuActivity.class.getSimpleName();

    private static final int REQUEST_ITEM_DETAILS = 15;


    @Inject
    RestaurantProvider restaurantProvider;

    @Inject
    SessionManager sessionManager;

    private FirebaseAuth mAuth;

    private OrdersFragment ordersFragment;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) { // don't even bother
            goToLogin();
            return;

        }

        setContentView(R.layout.activity_menu_selection);
        NeatApplication.getComponent(this).application().createSessionComponent().inject(this);

        ButterKnife.bind(this);

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

        setTableParticipants();

    }

    private void setTableParticipants() {
        tableParticipantsView.setText(getString(R.string.table_participants, "5", mAuth.getCurrentUser().getDisplayName()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (restaurant == null)
            restaurantProvider.getRestaurant("lateral", this);
        if (sessionManager != null)
            sessionManager.addOnOrdersPlacedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionManager != null)
            sessionManager.removeOnOrdersPlacedListener(this);
    }

    @Override
    public void onRestaurantLoaded(Restaurant restaurant) {

        this.restaurant = restaurant;

        sessionManager.newSession(restaurant, mAuth.getCurrentUser().getUid(), "tableId");

        ordersFragment.setStateHidden();

        Glide.with(this).load(restaurant.headerUrl)
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
        // Firebase sign out
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        goToLogin();
    }

    public void displayItemDetailsView(View view, Item item) {

        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra(ItemDetailsActivity.EXTRA_ITEM, item);

        View navigationBar = findViewById(android.R.id.navigationBarBackground);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,
//                        Pair.create(view.findViewById(R.id.item_title), "item_title"),
                        Pair.create(view.findViewById(R.id.item_image), "item_image"),
                        Pair.create(view.findViewById(R.id.item_layout), "item_layout"),
                        Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)
                );

        startActivityForResult(intent, REQUEST_ITEM_DETAILS, options.toBundle());

//        final FragmentManager fragmentManager = getFragmentManager();
//
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        itemDetailsFragment = ItemDetailsFragment.newInstance(item);
//        itemDetailsFragment.setOnItemDetailsClosedListener(this);
////        fragment.setSharedElementEnterTransition(new AutoTransition());
//        itemDetailsFragment.setEnterTransition(new Slide(Gravity.END));
//        transaction.addSharedElement(view.findViewById(R.id.item_title), "item_title");
//        transaction.addSharedElement(view.findViewById(R.id.item_image), "item_image");
//        transaction.replace(R.id.main_layout, itemDetailsFragment, null);
//        transaction.addToBackStack("item_details");
//        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ITEM_DETAILS) {
            if (resultCode == ItemDetailsActivity.RESULT_ITEM_ADDED) {
                sessionManager.addPendingItem(
                        (Item) data.getSerializableExtra(ItemDetailsActivity.EXTRA_ITEM),
                        data.getIntExtra(ItemDetailsActivity.EXTRA_ITEM_COUNT, -1));
            }
        }

    }

    @Override
    public void onOrdersPlaced(List<Order> newlyPlacedOrders) {
        /*
         * Reveal pay action
         */
        if (payAction.getVisibility() != View.VISIBLE) {
            payAction.setVisibility(View.VISIBLE);
            ViewAnimationUtils.createCircularReveal(
                    payAction,
                    payAction.getMeasuredWidth() / 2,
                    payAction.getMeasuredHeight() / 2, 0,
                    Math.max(payAction.getWidth(), payAction.getHeight()) / 2)
                    .start();
        }
    }
}
