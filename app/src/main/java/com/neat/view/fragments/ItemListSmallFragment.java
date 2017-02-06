package com.neat.view.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.neat.view.MenuActivity;
import com.neat.NeatApplication;
import com.neat.R;
import com.neat.model.SessionManager;
import com.neat.databinding.ItemMenuSmallBinding;
import com.neat.databinding.LayoutSectionHeaderBinding;
import com.neat.model.classes.Item;
import com.neat.model.classes.MenuSection;
import com.neat.viewmodel.AddItemToOrdersHandler;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemListSmallFragment extends Fragment implements AddItemToOrdersHandler {

    private static final String ARG_SECTION = "ARG_SECTION";

    MenuActivity menuActivity;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof MenuActivity)) {
            throw new RuntimeException("This fragment must be created in a MenuActivity");
        }
        menuActivity = (MenuActivity) context;
    }

    public ItemListSmallFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NeatApplication.getComponent(getActivity()).application().getSessionComponent().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        section = (MenuSection) getArguments().getSerializable(ARG_SECTION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_list_small, container, false);
        ButterKnife.bind(this, view);

        LayoutSectionHeaderBinding headerBinding = LayoutSectionHeaderBinding.bind(header);
        headerBinding.setSection(section);

        for (Item item : section.featuredItems) {
            ItemMenuSmallBinding itemListSmallBinding = ItemMenuSmallBinding.inflate(inflater);
            itemListSmallBinding.setItem(item);
            itemListSmallBinding.setHandlers(this);
            this.container.addView(itemListSmallBinding.getRoot());
        }

        return view;
    }

    @Override
    public void onItemClick(View view, Item item) {
        sessionManager.addPendingItem(item, 1);
    }

    @Override
    public void onDirectAddItemButtonClicked(View view, Item item) {
    }
}
