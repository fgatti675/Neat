package com.neat.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.neat.NeatApplication;
import com.neat.R;
import com.neat.dagger.SessionScope;
import com.neat.databinding.ActivityPaymentBinding;
import com.neat.databinding.ItemListRequestedOrderBinding;
import com.neat.model.SessionManager;
import com.neat.model.UserManager;
import com.neat.model.classes.Item;
import com.neat.model.classes.Order;
import com.neat.viewmodel.PaymentViewModel;
import com.neat.viewmodel.RequestedOrderViewModel;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

@SessionScope
public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = PaymentActivity.class.getSimpleName();

    @Inject
    SessionManager sessionManager;

    @Inject
    UserManager userManager;

    private ActivityPaymentBinding binding;
    private PaymentViewModel viewmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        NeatApplication.getComponent(this).application().getSessionComponent().inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        viewmodel = new PaymentViewModel(this, sessionManager, userManager);
        binding.setViewmodel(viewmodel);

        binding.toolbar.setTitle(R.string.pay);
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_dark_24dp);

        for (Order order : sessionManager.getSession().requestedOrders) {
            addRequestedOrderView(order);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewmodel.destroy();
    }

    Map<Item, RequestedOrderViewModel> itemRequestedOrderViewModelMap = new HashMap<>();

    private void addRequestedOrderView(Order order) {
        RequestedOrderViewModel requestedOrderViewModel = itemRequestedOrderViewModelMap.get(order.item);
        if (requestedOrderViewModel == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            ItemListRequestedOrderBinding itemListRequestedOrderBinding = ItemListRequestedOrderBinding.inflate(inflater);
            requestedOrderViewModel = new RequestedOrderViewModel(sessionManager, order.item);
            requestedOrderViewModel.addOrder(order);
            itemRequestedOrderViewModelMap.put(order.item, requestedOrderViewModel);
            itemListRequestedOrderBinding.setOrderviewmodel(requestedOrderViewModel);
            View view = itemListRequestedOrderBinding.getRoot();
            binding.requestedItemsLayout.addView(view, 0);
        } else {
            requestedOrderViewModel.addOrder(order);
        }
    }

}
