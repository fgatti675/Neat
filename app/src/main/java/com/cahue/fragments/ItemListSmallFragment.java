package com.cahue.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cahue.R;
import com.cahue.databinding.ItemListSmallBinding;
import com.cahue.databinding.LayoutSectionHeaderBinding;
import com.cahue.model.Item;
import com.cahue.model.MenuSection;
import com.cahue.util.ItemClickHandler;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemListSmallFragment extends Fragment {

    private static final String ARG_SECTION = "ARG_SECTION";

    private OrdersFragment ordersFragment;

    MenuSection section;

    @Bind(R.id.container)
    LinearLayout container;

    @Bind(R.id.header)
    View header;

    public static ItemListSmallFragment newInstance(MenuSection section) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION, section);
        ItemListSmallFragment fragment = new ItemListSmallFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ItemListSmallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        section = (MenuSection) getArguments().getSerializable(ARG_SECTION);
        ordersFragment = (OrdersFragment) getFragmentManager().findFragmentByTag(OrdersFragment.FRAGMENT_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_list_small, container, false);
        ButterKnife.bind(this, view);

        LayoutSectionHeaderBinding headerBinding = LayoutSectionHeaderBinding.bind(header);
        headerBinding.setSection(section);

        for (Item item : section.getBestSellingItems()) {
            ItemListSmallBinding itemListSmallBinding = ItemListSmallBinding.inflate(inflater);
            itemListSmallBinding.setItem(item);
            itemListSmallBinding.setHandlers(new ItemClickHandler());
            this.container.addView(itemListSmallBinding.getRoot());
        }

        return view;
    }

}
