package com.neat.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.neat.R;
import com.neat.model.classes.Session;

import java.io.Serializable;

/**
 * Created by f.gatti.gomez on 06/02/2017.
 */
public class RestaurantActivityViewModel extends BaseObservable implements Serializable {

    Context context;

    Session session;

    public RestaurantActivityViewModel(Context context, Session session) {
        this.context = context;
        this.session = session;
    }

    @Bindable
    public int getPayActionVisibility() {
        return session.requestedOrders.isEmpty() ? View.GONE : View.VISIBLE;
    }

    @Bindable
    public String getTableParticipantsText() {
        return context.getString(R.string.table_participants, session.table.name, session.users.values().iterator().next().name);
    }
}
