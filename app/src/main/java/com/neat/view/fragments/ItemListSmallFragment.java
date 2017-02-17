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
import com.neat.databinding.ItemMenuSmallBinding;
import com.neat.databinding.LayoutSectionHeaderBinding;
import com.neat.model.SessionManager;
import com.neat.model.classes.Item;
import com.neat.model.classes.MenuSection;
import com.neat.viewmodel.ItemViewModel;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemListSmallFragment extends Fragment {

    private static final String ARG_SECTION = "ARG_SECTION";

    MenuSection section;

    @Inject
    SessionManager sessionManager;

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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        NeatApplication.getComponent(getActivity()).application().getSessionComponent().inject(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_list_small, container, false);
        ButterKnife.bind(this, view);

        LayoutSectionHeaderBinding headerBinding = LayoutSectionHeaderBinding.bind(header);
        headerBinding.setSection(section);

        for (Item item : section.featuredItems) {
            ItemMenuSmallBinding itemListSmallBinding = ItemMenuSmallBinding.inflate(inflater);
            itemListSmallBinding.setItemViewModel(new ItemViewModel(getActivity(), sessionManager, item));
            this.container.addView(itemListSmallBinding.getRoot());
        }

        return view;
    }
}
