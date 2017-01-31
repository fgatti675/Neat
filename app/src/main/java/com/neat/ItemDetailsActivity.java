package com.neat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.neat.databinding.ActivityItemDetailsBinding;
import com.neat.model.Item;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemDetailsActivity extends AppCompatActivity {

    public static final int RESULT_ITEM_ADDED = 10;

    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String EXTRA_ITEM_COUNT = "EXTRA_ITEM_COUNT";

    private Item item;

    @Bind(R.id.add_button)
    Button addButton;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

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
        // set an enter transition
//        getWindow().setEnterTransition(new ChangeImageTransform());

        setContentView(R.layout.activity_item_details);

        item = (Item) getIntent().getExtras().getSerializable(EXTRA_ITEM);

        // Inflate the layout for this fragment
        ButterKnife.bind(this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
//        toolbar.setPadding(0, ViewUtils.getStatusBarHeight(this), 0, 0);

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

        updateCountViewsState();

    }

    private void updateCountViewsState() {
        countDownButton.setEnabled(orderCount > 1);
        countUpButton.setEnabled(orderCount < 100);
        countField.setText(String.valueOf(orderCount));
    }
}
