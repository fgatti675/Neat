package com.cahue;

import android.animation.Animator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.cahue.fragments.ItemFeaturedFragment;
import com.cahue.fragments.ItemListFragment;
import com.cahue.fragments.ItemListSmallFragment;
import com.cahue.fragments.OrdersFragment;
import com.cahue.model.Bill;
import com.cahue.model.MenuSection;
import com.cahue.model.Restaurant;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;

@Module
public class RestaurantActivity extends AppCompatActivity {

    private static final String TAG = RestaurantActivity.class.getSimpleName();

    private Restaurant restaurant;

    private Bill bill;

    private OrdersFragment ordersFragment;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.pay_button)
    Button payButton;

    @Bind(R.id.subtitle)
    TextView subtitle;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.app_bar)
    AppBarLayout appBarLayout;

    @Inject
    public RestaurantActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restaurant = RestaurantProvider.getLateral();
        bill = new Bill();

        setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);

        ordersFragment = (OrdersFragment) getFragmentManager().findFragmentById(R.id.orders_fragment);
        ordersFragment.setBill(bill);

        toolbar.inflateMenu(R.menu.menu_restaurant);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_menu) {
                    return true;
                }
                return false;
            }
        });

        final View payAction = toolbar.findViewById(R.id.action_pay);
        final float offsetX = getResources().getDimension(R.dimen.pay_button_toolbar_offset_x_collapsed);
        final float offsetY = getResources().getDimension(R.dimen.pay_button_toolbar_offset_y_expanded);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float scrollPercentage = 1 + ((float) verticalOffset) / (float) appBarLayout.getTotalScrollRange();
                float translationX = -(1 - scrollPercentage) * offsetX;
                float translationY = scrollPercentage * -offsetY;
                payButton.setTranslationX(translationX);
                payButton.setTranslationY(translationY);
                payButton.setAlpha(scrollPercentage);
                payAction.setAlpha(1 - scrollPercentage);
                subtitle.setAlpha((scrollPercentage - 0.5F) * 2);
            }
        });


        collapsingToolbarLayout.setTitle(restaurant.name);
        subtitle.setText(restaurant.subtitle);

        FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (MenuSection section : restaurant.menu.sections) {
            if (section.type != null) {
                switch (section.type) {
                    case TAGS:
                        transaction.add(R.id.main_content_container, ItemListSmallFragment.newInstance(section), null);
                        break;
                    case FEATURED:
                        transaction.add(R.id.main_content_container, ItemFeaturedFragment.newInstance(section), null);
                        break;
                    case LIST:
                        transaction.add(R.id.main_content_container, ItemListFragment.newInstance(section), null);
                        break;

                }
            }
            // default
            else {
                // TODO
            }

        }
        transaction.commit();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            payButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onGlobalLayout() {

                    // get the center for the clipping circle
                    int cx = payButton.getMeasuredWidth() / 2;
                    int cy = payButton.getMeasuredHeight() / 2;

                    // get the final radius for the clipping circle
                    int finalRadius = Math.max(payButton.getWidth(), payButton.getHeight()) / 2;

                    // create the animator for this view (the start radius is zero)
                    Animator anim = ViewAnimationUtils.createCircularReveal(payButton, cx, cy, 0, finalRadius);

                    // make the view visible and start the animation
                    payButton.setVisibility(View.VISIBLE);
                    anim.start();

                    payButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

    }

}
