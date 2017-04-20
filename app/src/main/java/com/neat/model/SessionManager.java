package com.neat.model;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.neat.dagger.SessionScope;
import com.neat.model.classes.CreditCard;
import com.neat.model.classes.Item;
import com.neat.model.classes.Order;
import com.neat.model.classes.Restaurant;
import com.neat.model.classes.Session;
import com.neat.model.classes.User;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import static com.neat.model.FireBasePaths.ACTIVE;
import static com.neat.model.FireBasePaths.CARD_ID;
import static com.neat.model.FireBasePaths.CREATION_DATE;
import static com.neat.model.FireBasePaths.CREATOR_ID;
import static com.neat.model.FireBasePaths.CURRENCY;
import static com.neat.model.FireBasePaths.CURRENT_SESSION;
import static com.neat.model.FireBasePaths.ITEM_ID;
import static com.neat.model.FireBasePaths.PAID;
import static com.neat.model.FireBasePaths.PAID_BY;
import static com.neat.model.FireBasePaths.PAYMENT;
import static com.neat.model.FireBasePaths.PENDING_ORDERS;
import static com.neat.model.FireBasePaths.REQUESTED_ORDERS;
import static com.neat.model.FireBasePaths.REQUESTER_ID;
import static com.neat.model.FireBasePaths.RESTAURANTS;
import static com.neat.model.FireBasePaths.SESSIONS;
import static com.neat.model.FireBasePaths.SPECIAL_INSTRUCTIONS;
import static com.neat.model.FireBasePaths.TABLES;
import static com.neat.model.FireBasePaths.TABLE_ID;
import static com.neat.model.FireBasePaths.USERS;

/**
 * Created by f.gatti.gomez on 10/10/16.
 */
@SessionScope
public class SessionManager {

    public static final String TAG = SessionManager.class.getSimpleName();

    private User user;
    private DatabaseReference restaurantRef;
    private UserManager userManager;

    public interface OnPendingOrdersChangedListener {

        void onPendingOrderCreated(Order order);

        void onPendingOrderRemoved(Order order);

    }

    public interface OnOrdersPlacedListener {

        void onOrdersPlaced(Set<Order> newlyPlacedOrders);

        void onOrdersPlacedError();
    }

    public interface OnSessionJoinedCallbacks {

        void onSessionJoined(Session session);

        void onSessionJoinFail();
    }

    public interface OnPaymentCallbacks {

        void onSessionPayed(Session session);

        void onSessionPaymentFail();
    }

    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private Session session;
    private Restaurant restaurant;

    private DatabaseReference sessionRef;

    private List<OnOrdersPlacedListener> onOrdersPlacedListeners = new LinkedList<>();
    private List<OnPendingOrdersChangedListener> onPendingOrdersChangedListeners = new LinkedList<>();
    private List<OnSessionJoinedCallbacks> sessionJoinedCallbacks = new LinkedList<>();
    private List<OnPaymentCallbacks> sessionPaidCallbacks = new LinkedList<>();

    @Inject
    public SessionManager(@Named("logged_user") User user, UserManager userManager) {

        if (user == null)
            throw new NullPointerException("A logged user must be provided to use this component");

        this.user = user;
        this.userManager = userManager;
    }

    public Session getSession() {
        return session;
    }

    public boolean hasSessionStarted() {
        return session != null;
    }

    public void joinSession(final Restaurant restaurant, final String tableId) {
        Log.d(TAG, "joinSession: ");
        if (sessionJoinedCallbacks.isEmpty()) {
            Log.w(TAG, "You are joining a sessions but there are no session listeners attached");
        }
        this.restaurant = restaurant;
        restaurantRef = database.child(RESTAURANTS).child(restaurant.id);
        restaurantRef.child(TABLES).child(tableId).child(CURRENT_SESSION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // session is already existing
                if (dataSnapshot.exists()) {
                    joinSessionAsync(restaurant, (String) dataSnapshot.getValue());
                }

                // session must be created
                else {
                    createNewSessionAsync(restaurant, tableId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                for (OnSessionJoinedCallbacks callbacks : sessionJoinedCallbacks)
                    callbacks.onSessionJoinFail();
            }
        });


    }

    private void joinSessionAsync(final Restaurant restaurant, final String sessionId) {
        Log.d(TAG, "joinSessionAsync: ");
        sessionRef = restaurantRef.child(SESSIONS).child(sessionId);
        sessionRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData sessionData) {
                sessionData.child(USERS).child(user.uid).setValue(user);
                return Transaction.success(sessionData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot sessionSnapshot) {
                Log.d(TAG, "joinSessionAsync : onComplete: ");
                if (databaseError != null) {
                    for (OnSessionJoinedCallbacks callbacks : sessionJoinedCallbacks)
                        callbacks.onSessionJoinFail();
                } else {
                    session = convertSessionFrom(restaurant, sessionSnapshot);
                    setUpListeners();
                    for (OnSessionJoinedCallbacks callbacks : sessionJoinedCallbacks)
                        callbacks.onSessionJoined(session);
                }
            }
        });
    }

    private void createNewSessionAsync(final Restaurant restaurant, final String tableId) {
        Log.d(TAG, "createNewSessionAsync: ");
        sessionRef = restaurantRef.child(SESSIONS).push();
        sessionRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData sessionData) {
                restaurantRef.child(TABLES).child(tableId).child(CURRENT_SESSION).setValue(sessionRef.getKey());
                sessionData.child(CURRENCY).setValue(restaurant.menu.currency);
                sessionData.child(TABLE_ID).setValue(tableId);
                sessionData.child(USERS).child(user.uid).setValue(user);
                sessionData.child(CREATION_DATE).setValue(ServerValue.TIMESTAMP);
                sessionData.child(PAYMENT).child(PAID).setValue(false);
                sessionData.child(ACTIVE).setValue(true);
                return Transaction.success(sessionData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot sessionSnapshot) {
                if (databaseError != null) {
                    for (OnSessionJoinedCallbacks callbacks : sessionJoinedCallbacks)
                        callbacks.onSessionJoinFail();
                } else {
                    session = convertSessionFrom(restaurant, sessionSnapshot);
                    setUpListeners();
                    for (OnSessionJoinedCallbacks callbacks : sessionJoinedCallbacks)
                        callbacks.onSessionJoined(session);
                }
            }
        });
    }


    public void addPendingOrder(final Item item, final String instructions) { // TODO: add item config

        if (session == null)
            throw new IllegalStateException("Session has not been joined");

        sessionRef.child(PENDING_ORDERS).push().runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.child(ITEM_ID).setValue(item.id);
                mutableData.child(CREATOR_ID).setValue(user.uid);
                mutableData.child(SPECIAL_INSTRUCTIONS).setValue(instructions);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    public void removePendingOrder(Order order) {

        if (session == null)
            throw new IllegalStateException("Session has not been joined");

        sessionRef.child(PENDING_ORDERS).child(order.id).removeValue();
    }

    public void placePendingOrders() {

        sessionRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                // TODO: not working in case of db error
                // this prevents the removed listener to trigger
                session.pendingOrders.clear();

                for (MutableData pendingOrderMutableData : mutableData.child(PENDING_ORDERS).getChildren()) {
                    String key = pendingOrderMutableData.getKey();
                    MutableData requestedOrderMutableData = mutableData.child(REQUESTED_ORDERS).child(key);
                    requestedOrderMutableData.child(ITEM_ID).setValue(pendingOrderMutableData.child(ITEM_ID).getValue());
                    requestedOrderMutableData.child(SPECIAL_INSTRUCTIONS).setValue(pendingOrderMutableData.child(SPECIAL_INSTRUCTIONS).getValue());
                    requestedOrderMutableData.child(REQUESTER_ID).setValue(pendingOrderMutableData.child(REQUESTER_ID).getValue());
                }

                mutableData.child(PENDING_ORDERS).setValue(null);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    public void pay(final CreditCard creditCard) {

        userManager.getUserToken(new UserManager.TokenFetchCallback() {
            @Override
            public void onTokenFetchedResult(String token) {

            }

            @Override
            public void onTokenFetchError(Exception e) {

            }
        });

        sessionRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                mutableData.child(ACTIVE).setValue(false);
                mutableData.child(PAYMENT).child(PAID).setValue(true);
                mutableData.child(PAYMENT).child(PAID_BY).setValue(user.uid);
                mutableData.child(PAYMENT).child(CARD_ID).setValue(creditCard.id);
                restaurantRef.child(TABLES).child(session.table.id).child(CURRENT_SESSION).removeValue();

                // this prevents the removed listener to trigger
                session.pendingOrders.clear();

                for (MutableData pendingOrderMutableData : mutableData.child(PENDING_ORDERS).getChildren()) {
                    String key = pendingOrderMutableData.getKey();
                    MutableData requestedOrderMutableData = mutableData.child(REQUESTED_ORDERS).child(key);
                    requestedOrderMutableData.child(ITEM_ID).setValue(pendingOrderMutableData.child(ITEM_ID).getValue());
                    requestedOrderMutableData.child(SPECIAL_INSTRUCTIONS).setValue(pendingOrderMutableData.child(SPECIAL_INSTRUCTIONS).getValue());
                    requestedOrderMutableData.child(REQUESTER_ID).setValue(pendingOrderMutableData.child(REQUESTER_ID).getValue());
                }

                mutableData.child(PENDING_ORDERS).setValue(null);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                for (OnPaymentCallbacks callbacks : sessionPaidCallbacks)
                    callbacks.onSessionPayed(convertSessionFrom(restaurant, dataSnapshot));
            }
        });
    }

    public void addOnOrdersPlacedListener(OnOrdersPlacedListener e) {
        onOrdersPlacedListeners.add(e);
    }

    public void removeOnOrdersPlacedListener(OnOrdersPlacedListener e) {
        onOrdersPlacedListeners.remove(e);
    }

    public void addOnPendingOrdersChangedListener(OnPendingOrdersChangedListener e) {
        onPendingOrdersChangedListeners.add(e);
    }

    public void removeOnPendingOrdersChangedListener(OnPendingOrdersChangedListener e) {
        onPendingOrdersChangedListeners.remove(e);
    }

    public void addOnSessionJoinedCallbacks(OnSessionJoinedCallbacks onSessionJoinedCallbacks) {
        sessionJoinedCallbacks.add(onSessionJoinedCallbacks);
    }

    public void removeOnSessionJoinedCallbacks(OnSessionJoinedCallbacks onSessionJoinedCallbacks) {
        sessionJoinedCallbacks.remove(onSessionJoinedCallbacks);
    }

    public void addOnSessionPaidCallbacks(OnPaymentCallbacks onPaymentCallbacks) {
        sessionPaidCallbacks.add(onPaymentCallbacks);
    }

    public void removeOnSessionPaidCallbacks(OnPaymentCallbacks onPaymentCallbacks) {
        sessionPaidCallbacks.remove(onPaymentCallbacks);
    }

    private static Session convertSessionFrom(final Restaurant restaurant, DataSnapshot sessionSnapshot) {
        Session session = new Session();
        session.id = sessionSnapshot.getKey();
        session.restaurant = restaurant;
        session.currency = (String) sessionSnapshot.child(CURRENCY).getValue();
        session.table = restaurant.tables.get(sessionSnapshot.child(TABLE_ID).getValue());
        session.creationDate = new Date((long) sessionSnapshot.child(CREATION_DATE).getValue());
        for (DataSnapshot userRef : sessionSnapshot.child(USERS).getChildren()) {
            User user = userRef.getValue(User.class);
            user.uid = userRef.getKey();
            session.users.put(userRef.getKey(), user);
        }
        for (DataSnapshot orderRef : sessionSnapshot.child(PENDING_ORDERS).getChildren()) {
            session.pendingOrders.add(convertOrderFrom(restaurant, session, orderRef));
        }
        for (DataSnapshot orderRef : sessionSnapshot.child(REQUESTED_ORDERS).getChildren()) {
            session.requestedOrders.add(convertOrderFrom(restaurant, session, orderRef));
        }
        return session;
    }

    private static Order convertOrderFrom(Restaurant restaurant, Session session, DataSnapshot dataSnapshot) {
        Order order = new Order();
        order.id = dataSnapshot.getKey();
        order.item = restaurant.menu.items.get(dataSnapshot.child(ITEM_ID).getValue());
        order.specialInstructions = (String) dataSnapshot.child(SPECIAL_INSTRUCTIONS).getValue();
        order.creator = session.users.get(dataSnapshot.child(CREATOR_ID).getValue());
        return order;
    }


    private void setUpListeners() {
        /*
         * Pending orders listener
         */
        sessionRef.child(PENDING_ORDERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Order order = convertOrderFrom(restaurant, session, dataSnapshot);
                if (session.pendingOrders.add(order)) {
                    for (OnPendingOrdersChangedListener listener : onPendingOrdersChangedListeners)
                        listener.onPendingOrderCreated(order);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                    Order order = convertOrderFrom(restaurant, session, childSnapshot);
//                    session.pendingOrders.add(order);
//                    for (OnPendingOrdersChangedListener listener : onPendingOrdersChangedListeners)
//                        listener.onPendingOrderChanged(order);
//                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Order order = convertOrderFrom(restaurant, session, dataSnapshot);
                if (session.pendingOrders.remove(order)) {
                    for (OnPendingOrdersChangedListener listener : onPendingOrdersChangedListeners)
                        listener.onPendingOrderRemoved(order);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
         * Requested orders listener
         */
        sessionRef.child(REQUESTED_ORDERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) return;

                Set<Order> newlyPlacedOrders = new LinkedHashSet<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Order order = convertOrderFrom(restaurant, session, childSnapshot);
                    if (session.requestedOrders.add(order)) {
                        session.pendingOrders.remove(order);
                        newlyPlacedOrders.add(order);
                    }
                }

                if (!newlyPlacedOrders.isEmpty()) {
                    for (OnOrdersPlacedListener listener : onOrdersPlacedListeners) {
                        listener.onOrdersPlaced(newlyPlacedOrders);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                for (OnOrdersPlacedListener listener : onOrdersPlacedListeners)
                    listener.onOrdersPlacedError();
            }
        });

//        /*
//         * Global orders listener
//         */
//        sessionRef.child(ORDERS).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.getValue() == null) return;
//
//                Set<Order> prevPendingOrders = session.pendingOrders;
//                session.pendingOrders = new LinkedHashSet<>();
//
//                for (DataSnapshot childSnapshot : dataSnapshot.child(PENDING_ORDERS).getChildren()) {
//                    Order order = convertOrderFrom(restaurant, session, childSnapshot);
//                    session.pendingOrders.add(order);
//                }
//
//                // check for new pending elements
//                for (Order order : session.pendingOrders) {
//                    if (!prevPendingOrders.contains(order)) {
//                        for (OnPendingOrdersChangedListener listener : onPendingOrdersChangedListeners)
//                            listener.onPendingOrderCreated(order);
//                    }
//                }
//
//                // check for new requested elements
//                Set<Order> newlyPlacedOrders = new LinkedHashSet<>();
//
//                for (DataSnapshot childSnapshot : dataSnapshot.child(REQUESTED_ORDERS).getChildren()) {
//                    Order order = convertOrderFrom(restaurant, session, childSnapshot);
//                    if (session.requestedOrders.add(order)) {
//                        newlyPlacedOrders.add(order);
//                    }
//                }
//
//                for (OnOrdersPlacedListener listener : onOrdersPlacedListeners) {
//                    listener.onOrdersPlaced(newlyPlacedOrders);
//                }
//
//                // check for removed elements
//                for (Order prevPendingOrder : prevPendingOrders) {
//                    if (!session.pendingOrders.contains(prevPendingOrder)) {
//                        for (OnPendingOrdersChangedListener listener : onPendingOrdersChangedListeners)
//                            listener.onPendingOrderRemoved(prevPendingOrder);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                for (OnOrdersPlacedListener listener : onOrdersPlacedListeners)
//                    listener.onOrdersPlacedError();
//            }
//        });
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void setUser(User user) {
        this.user = user;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public void deleteSession() {
        sessionRef.removeValue();
        restaurantRef.child(TABLES).child(session.table.id).child(CURRENT_SESSION).removeValue();
    }
}
