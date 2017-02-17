package com.neat;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.neat.model.RestaurantProvider;
import com.neat.model.SessionManager;
import com.neat.model.classes.Item;
import com.neat.model.classes.Order;
import com.neat.model.classes.Restaurant;
import com.neat.model.classes.Session;
import com.neat.model.classes.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * Created by f.gatti.gomez on 12/02/2017.
 */
@RunWith(AndroidJUnit4.class)
public class SessionManagerTest {

    SessionManager sessionManager;

    RestaurantProvider restaurantProvider = new RestaurantProvider();

    Restaurant restaurant;

    private Session session;
    private User user;
    private String tableId;

    @Before
    public void setUp() throws Exception {

        user = new User();
        user.name = "Test user";
        user.email = "test@me.com";
        user.uid = "abcdef";

        sessionManager = new SessionManager(user);

        final Object lock = new Object();

        synchronized (lock) {
            restaurantProvider.getRestaurant("lateral", new RestaurantProvider.Callback() {
                @Override
                public void onRestaurantLoaded(Restaurant r) {
                    synchronized (lock) {
                        restaurant = r;
                        lock.notify();
                    }
                }
            });
            lock.wait();
        }
        tableId = restaurant.tables.values().iterator().next().id;

        joinSession();
    }

    private void joinSession() throws InterruptedException {

        final Object lock = new Object();
        synchronized (lock) {
            SessionManager.OnSessionJoinedCallbacks callbacks = new SessionManager.OnSessionJoinedCallbacks() {

                @Override
                public void onSessionJoined(Session session) {
                    synchronized (lock) {
                        SessionManagerTest.this.session = session;
                        Log.d("test", "onSessionJoined: ");
                        lock.notify();
                    }
                }

                @Override
                public void onSessionJoinFail() {
                    synchronized (lock) {
                        Log.d("test", "onSessionJoinFail: ");
                        lock.notify();
                    }
                }
            };
            sessionManager.addOnSessionJoinedCallbacks(callbacks);
            sessionManager.joinSession(restaurant, tableId);
            lock.wait();
        }
    }


    @After
    public void tearDown() throws Exception {
        sessionManager.deleteSession();
    }

    @Test
    public void testCreateSession() throws Exception {
        assertNotNull(session);
        assertThat(session.users.values(), contains(user));
        Log.d("test", "testJoinSession: ");
    }

    @Test
    public void testJoinSession() throws Exception {
        assertNotNull(session);

        User user2 = new User();
        user2.name = "Test user 2";
        user2.email = "test@me2.com";
        user2.uid = "abcdef2";
        sessionManager.setUser(user2);
        joinSession();

        Collection<User> users = session.users.values();
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));
        Log.d("test", "testJoinSession: ");
    }

    @Test
    public void testAddPendingItem() throws Exception {

        final Item item = restaurant.menu.items.values().iterator().next();

        final Object lock = new Object();

        synchronized (lock) {
            sessionManager.addOnPendingOrdersChangedListener(new SessionManager.OnPendingOrdersChangedListener() {
                @Override
                public void onPendingOrderCreated(Order order) {
                    synchronized (lock) {
                        assertEquals(item, order.item);
                        lock.notify();
                    }
                }
                @Override
                public void onPendingOrderRemoved(Order order) {
                }
            });
            sessionManager.addPendingOrder(item, "Make it tasty!");
            lock.wait();
        }

        assertEquals(item, sessionManager.getSession().pendingOrders.iterator().next().item);
    }

    @Test
    public void testRemovePendingOrder() throws Exception {

        final Item item = restaurant.menu.items.values().iterator().next();

        final Object lock = new Object();

        synchronized (lock) {
            sessionManager.addOnPendingOrdersChangedListener(new SessionManager.OnPendingOrdersChangedListener() {
                @Override
                public void onPendingOrderCreated(Order order) {
                    sessionManager.removePendingOrder(order);
                }

                @Override
                public void onPendingOrderRemoved(Order order) {
                    synchronized (lock) {
                        assertEquals(item, order.item);
                        lock.notify();
                    }
                }
            });
            sessionManager.addPendingOrder(item, "Make it tasty!");
            lock.wait();
        }

        assertTrue(sessionManager.getSession().pendingOrders.isEmpty());
    }

    @Test
    public void testPlacePendingOrders() throws Exception {

        Iterator<Item> iterator = restaurant.menu.items.values().iterator();
        final Item item = iterator.next();
        final Item item2 = iterator.next();

        final Object lock = new Object();

        synchronized (lock) {

            sessionManager.addPendingOrder(item, "Make it tasty!");
            sessionManager.addPendingOrder(item2, "Quick!");
            sessionManager.addPendingOrder(item2, "Extra chocolate");

            sessionManager.addOnPendingOrdersChangedListener(new SessionManager.OnPendingOrdersChangedListener() {
                @Override
                public void onPendingOrderCreated(Order order) {
                }

                @Override
                public void onPendingOrderRemoved(Order order) {
                    fail();
                }
            });

            sessionManager.addOnOrdersPlacedListener(new SessionManager.OnOrdersPlacedListener() {
                @Override
                public void onOrdersPlaced(Set<Order> newlyPlacedOrders) {
                    synchronized (lock) {
                        Iterator<Order> orderIterator = newlyPlacedOrders.iterator();
                        assertEquals(item, orderIterator.next().item);
                        assertEquals(item2, orderIterator.next().item);
                        assertEquals(item2, orderIterator.next().item);
                        lock.notify();
                    }
                }

                @Override
                public void onOrdersPlacedError() {

                }
            });
            sessionManager.placePendingOrders();
            lock.wait();
        }

        assertTrue(sessionManager.getSession().pendingOrders.isEmpty());
        assertTrue(sessionManager.getSession().requestedOrders.size() == 3);
    }

}