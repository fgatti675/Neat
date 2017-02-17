package com.neat.view.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.neat.NeatApplication;
import com.neat.R;
import com.neat.databinding.ItemMenuRightBinding;
import com.neat.databinding.LayoutSectionHeaderBinding;
import com.neat.model.SessionManager;
import com.neat.model.classes.Item;
import com.neat.model.classes.MenuSection;
import com.neat.viewmodel.ItemViewModel;

import java.util.Iterator;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemListFragment extends Fragment {

    private static final String ARG_SECTION = "ARG_SECTION";

    MenuSection section;

    @Inject
    SessionManager sessionManager;

    @Bind(R.id.header)
    View header;

    @Bind(R.id.container)
    LinearLayout container;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        NeatApplication.getComponent(getActivity()).application().getSessionComponent().inject(this);

        View view = inflater.inflate(R.layout.fragment_items_list, container, false);
        ButterKnife.bind(this, view);

        LayoutSectionHeaderBinding headerBinding = LayoutSectionHeaderBinding.bind(header);
        headerBinding.setSection(section);
        Iterator<Item> iterator = section.items.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            ItemMenuRightBinding itemTagBinding = ItemMenuRightBinding.inflate(inflater);
            itemTagBinding.setItemViewModel(new ItemViewModel(getActivity(), sessionManager, item));
            this.container.addView(itemTagBinding.getRoot());

            if (iterator.hasNext()) {
                View divider = inflater.inflate(R.layout.list_divider, container, false);
                this.container.addView(divider);
            }
        }

        return view;
    }
}
