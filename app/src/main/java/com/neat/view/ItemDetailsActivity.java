package com.neat.view;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.neat.R;
import com.neat.databinding.ActivityItemDetailsBinding;
import com.neat.model.classes.Item;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemDetailsActivity extends AppCompatActivity {

    public static final int RESULT_ITEM_ADDED = 10;

    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String EXTRA_ITEM_COUNT = "EXTRA_ITEM_COUNT";

    private Item item;

    @Bind(R.id.add_button)
    Button addButton;

    @Bind(R.id.bottom_layout)
    FrameLayout bottomLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.item_image)
    ImageView itemImage;

    @Bind(R.id.count_field)
    TextView countField;

    @Bind(R.id.count_up)
    ImageButton countUpButton;

    @Bind(R.id.count_down)
    ImageButton countDownButton;

    private int orderCount = 1;

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
                setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        getWindow().setSharedElementEnterTransition(sharedElementsTransition);
        getWindow().setSharedElementsUseOverlay(false);

        getWindow().setEnterTransition(new TransitionSet().
                setOrdering(TransitionSet.ORDERING_TOGETHER).
                addTransition(new Slide().excludeTarget(R.id.toolbar, true)).
                addTransition(new Fade()));

        setContentView(R.layout.activity_item_details);
        ButterKnife.bind(this);

        item = (Item) getIntent().getExtras().getSerializable(EXTRA_ITEM);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ITEM, item);
                intent.putExtra(EXTRA_ITEM_COUNT, orderCount);
                setResult(RESULT_ITEM_ADDED, intent);
                supportFinishAfterTransition();
            }
        });

        countUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderCount++;
                updateCountViewsState();
            }
        });
        countDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderCount--;
                updateCountViewsState();
            }
        });

        ActivityItemDetailsBinding binding = ActivityItemDetailsBinding.bind(findViewById(R.id.main_layout));
        binding.setItem(item);

        postponeEnterTransition();
        itemImage.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        itemImage.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });

        updateCountViewsState();

    }

    private void updateCountViewsState() {
        countDownButton.setEnabled(orderCount > 1);
        countUpButton.setEnabled(orderCount < 100);
        countField.setText(String.valueOf(orderCount));
    }
}
