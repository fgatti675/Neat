package com.cahue.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cahue.R;
import com.cahue.databinding.ItemSquareBinding;
import com.cahue.databinding.LayoutSectionHeaderBinding;
import com.cahue.model.MenuSection;
import com.cahue.util.ItemClickHandler;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemFeaturedFragment extends Fragment {

    private static final String ARG_SECTION = "ARG_SECTION";

    @Bind(R.id.header)
    View header;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    MenuSection section;

    public static ItemFeaturedFragment newInstance(MenuSection section) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION, section);
        ItemFeaturedFragment fragment = new ItemFeaturedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ItemFeaturedFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_featured, container, false);
        ButterKnife.bind(this, view);

        LayoutSectionHeaderBinding headerBinding = LayoutSectionHeaderBinding.bind(header);
        headerBinding.setSection(section);

        recyclerView.setAdapter(new Adapter());
        recyclerView.setFocusable(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }


    private class Adapter extends RecyclerView.Adapter<ItemViewHolder>{

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemViewHolder viewHolder = new ItemViewHolder(ItemSquareBinding.inflate(LayoutInflater.from(parent.getContext())));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            ItemSquareBinding binding = holder.getBinding();
            binding.setItem(section.items.get(position));
            binding.setHandlers(new ItemClickHandler());
        }

        @Override
        public int getItemCount() {
            return section.items.size();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemSquareBinding binding;

        public ItemViewHolder(ItemSquareBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ItemSquareBinding getBinding() {
            return binding;
        }
    }

}
