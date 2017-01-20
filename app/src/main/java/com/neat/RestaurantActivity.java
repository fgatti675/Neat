package com.neat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.neat.fragments.ItemFeaturedFragment;
import com.neat.fragments.ItemListFragment;
import com.neat.fragments.ItemListSmallFragment;
import com.neat.fragments.OrdersFragment;
import com.neat.model.MenuSection;
import com.neat.model.Restaurant;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RestaurantActivity extends AppCompatActivity implements RestaurantProvider.Callback {

    private static final String TAG = RestaurantActivity.class.getSimpleName();

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

    @Bind(R.id.pay_button)
    Button payButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) { // don't even bother
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_restaurant);

        NeatApplication.getComponent(this).inject(this);
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

        final View payAction = toolbar.findViewById(R.id.action_pay);
        final View menuAction = toolbar.findViewById(R.id.action_menu);

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
                payButton.setAlpha(scrollPercentage);
                payAction.setAlpha(1 - scrollPercentage);
                menuAction.setAlpha(1 - scrollPercentage);

                // fade out elements that disappear
                float normalizedPercentage = (scrollPercentage - 0.5F) * 2;
                subtitle.setAlpha(normalizedPercentage);
                headerCaret.setAlpha(normalizedPercentage);

                collapsingToolbarLayout.setPadding(0, 0, 0, (int) (tablesViewHeight * normalizedPercentage));

                if (scrollPercentage < .1) return;

                // display orders layout based on scroll
                ordersFragment.setBottomSheetDisplayedIfHasOrders(scrollPercentage < .5);

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
    }

    @Override
    public void onRestaurantLoaded(Restaurant restaurant) {

        this.restaurant = restaurant;

        sessionManager.newSession(restaurant);

        ordersFragment.setBottomSheetDisplayedIfHasOrders(false);

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
                        transaction.add(R.id.main_sections_container, ItemListFragment.newInstance(section), null);
                        break;

                }
            }
            // default
            else {
                // TODO
            }

        }
        transaction.commit();

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
}
