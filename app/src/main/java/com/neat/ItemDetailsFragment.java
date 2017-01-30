package com.neat;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.neat.databinding.FragmentItemDetailsBinding;
import com.neat.model.Item;
import com.neat.util.ViewUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemDetailsFragment extends Fragment {

    public interface OnItemDetailsClosedListener {
        void onItemDetailsClosedRequested();
    }

    private static final String ARG_ITEM = "ARG_ITEM";

    private Item item;

    @Inject
    SessionManager sessionManager;

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

    OnItemDetailsClosedListener onItemDetailsClosedListener;

    private int orderCount = 1;

    public ItemDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param item
     * @return A new instance of fragment ItemDetailsFragment.
     */
    public static ItemDetailsFragment newInstance(Item item) {
        ItemDetailsFragment fragment = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        NeatApplication.getComponent(context).inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (Item) getArguments().getSerializable(ARG_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);
        ButterKnife.bind(this, view);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemDetailsClosedListener != null)
                    onItemDetailsClosedListener.onItemDetailsClosedRequested();
            }
        });
        toolbar.setPadding(0, ViewUtils.getStatusBarHeight(getActivity()), 0, 0);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.addPendingItem(item, orderCount);
                if (onItemDetailsClosedListener != null)
                    onItemDetailsClosedListener.onItemDetailsClosedRequested();
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

        FragmentItemDetailsBinding binding = FragmentItemDetailsBinding.bind(view);
        binding.setItem(item);

        updateCountViewsState();

        return view;

    }

    public void setOnItemDetailsClosedListener(RestaurantActivity onItemDetailsClosedListener) {
        this.onItemDetailsClosedListener = onItemDetailsClosedListener;
    }

    private void updateCountViewsState() {
        countDownButton.setEnabled(orderCount > 1);
        countUpButton.setEnabled(orderCount < 100);
        countField.setText(String.valueOf(orderCount));
    }

}
