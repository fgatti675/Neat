package com.neat.view;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Toolbar;

import com.neat.NeatApplication;
import com.neat.R;
import com.neat.databinding.ActivityItemDetailsBinding;
import com.neat.model.SessionManager;
import com.neat.model.classes.Item;
import com.neat.viewmodel.ItemDetailsViewModel;

import javax.inject.Inject;

public class ItemDetailsActivity extends AppCompatActivity {

    public static final int REQUEST_ITEM_DETAILS = 15;

    public static void startItemDetailsActivity(Activity activity, View view, Item item) {
        Intent intent = new Intent(activity, ItemDetailsActivity.class);
        intent.putExtra(ItemDetailsActivity.EXTRA_ITEM, item);

        View navigationBar = activity.findViewById(android.R.id.navigationBarBackground);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(activity,
//                        Pair.create(view.findViewById(R.id.item_title), "item_title"),
                        Pair.create(view.findViewById(R.id.item_image), "item_image"),
                        Pair.create(view.findViewById(R.id.item_layout), "item_layout"),
                        Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)
                );

        activity.startActivityForResult(intent, REQUEST_ITEM_DETAILS, options.toBundle());
    }

    public static final int RESULT_ITEM_ADDED = 10;

    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String EXTRA_ITEM_COUNT = "EXTRA_ITEM_COUNT";

    private Item item;

    @Inject
    SessionManager sessionManager;

    private ItemDetailsViewModel itemViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Transition sharedElementsTransition = new TransitionSet().
                addTransition(new Fade(Fade.OUT)).
                addTransition(new TransitionSet().
                        setOrdering(TransitionSet.ORDERING_TOGETHER).
                        addTransition(new ChangeImageTransform()).
                        addTransition(new ChangeBounds())
                ).
                addTransition(new Fade(Fade.IN)).
                setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
                .excludeTarget(Toolbar.class, true);

        getWindow().setSharedElementEnterTransition(sharedElementsTransition);
        getWindow().setSharedElementsUseOverlay(true);

        getWindow().setEnterTransition(new TransitionSet().
                setOrdering(TransitionSet.ORDERING_TOGETHER).
                addTransition(new Slide().excludeTarget(R.id.toolbar, true)).
                addTransition(new Fade()));

        item = (Item) getIntent().getExtras().getSerializable(EXTRA_ITEM);
        NeatApplication.getComponent(this).application().getSessionComponent().inject(this);

        final ActivityItemDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_item_details);
        itemViewModel = new ItemDetailsViewModel(this, sessionManager, item);
        binding.setItemViewModel(itemViewModel);


        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

        postponeEnterTransition();
        binding.itemImage.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        binding.itemImage.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });

    }

}
