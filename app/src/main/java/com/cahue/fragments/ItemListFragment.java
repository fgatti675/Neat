package com.cahue.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cahue.R;
import com.cahue.databinding.ItemListRightBinding;
import com.cahue.databinding.LayoutSectionHeaderBinding;
import com.cahue.model.Item;
import com.cahue.model.MenuSection;
import com.cahue.util.ItemClickHandler;

import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemListFragment extends Fragment {

    private static final String ARG_SECTION = "ARG_SECTION";

    MenuSection section;

    @Bind(R.id.header)
    View header;

    @Bind(R.id.container)
    LinearLayout container;

    private OrdersFragment ordersFragment;

    public static ItemListFragment newInstance(MenuSection section) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION, section);
        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ItemListFragment() {
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

        View view = inflater.inflate(R.layout.fragment_items_list, container, false);
        ButterKnife.bind(this, view);

        LayoutSectionHeaderBinding headerBinding = LayoutSectionHeaderBinding.bind(header);
        headerBinding.setSection(section);
        Iterator<Item> iterator = section.items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
//                ItemListBinding itemTagBinding = ItemListBinding.inflate(inflater);
//                itemTagBinding.setItem(item);
//                itemView = itemTagBinding.getRoot();
            ItemListRightBinding itemTagBinding = ItemListRightBinding.inflate(inflater);
            itemTagBinding.setItem(item);
            itemTagBinding.setHandlers(new ItemClickHandler());
            this.container.addView(itemTagBinding.getRoot());

            if (iterator.hasNext()) {
                View divider = inflater.inflate(R.layout.divider, container, false);
                this.container.addView(divider);
            }
        }

        return view;
    }

}
