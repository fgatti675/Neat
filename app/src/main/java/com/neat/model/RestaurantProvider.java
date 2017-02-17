package com.neat.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neat.model.classes.Item;
import com.neat.model.classes.Menu;
import com.neat.model.classes.MenuSection;
import com.neat.model.classes.Restaurant;
import com.neat.model.classes.Table;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;


/**
 * Created by f.gatti.gomez on 10/10/16.
 */
@Singleton
public class RestaurantProvider {

    public static final String HEADER_URL = "header_url";
    public static final String RESTAURANTS = "restaurants";
    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String MENU = "menu";
    public static final String CURRENCY = "currency";
    public static final String ITEMS = "items";
    public static final String SECTIONS = "sections";
    public static final String TABLES = "tables";
    public static final String HEADLINE = "headline";
    public static final String TYPE = "type";
    public static final String FEATURED_ITEMS = "featured_items";
    public static final String NAME = "name";
    public static final String CURRENT_SESSION = "current_session";

    @Inject
    public RestaurantProvider() {
    }

    public interface Callback {
        void onRestaurantLoaded(Restaurant restaurant);
    }

    private static final String TAG = "RestaurantProvider";

    public void getRestaurant(final String restaurantSlug, final Callback callback) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference restaurantRef = database.child(RESTAURANTS).child(restaurantSlug);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant restaurant = extractRestaurant(dataSnapshot);
                callback.onRestaurantLoaded(restaurant);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

    }

    @NonNull
    private Restaurant extractRestaurant(DataSnapshot dataSnapshot) {
        Restaurant restaurant = new Restaurant();
        restaurant.title = dataSnapshot.child(TITLE).getValue(String.class);
        restaurant.subtitle = dataSnapshot.child(SUBTITLE).getValue(String.class);
        restaurant.headerUrl = dataSnapshot.child(HEADER_URL).getValue(String.class);
        restaurant.id = dataSnapshot.getKey();

        restaurant.menu = new Menu();
        DataSnapshot menuSnapshot = dataSnapshot.child(MENU);
        restaurant.menu.currency = menuSnapshot.child(CURRENCY).getValue(String.class);
        DataSnapshot itemsSnapshot = menuSnapshot.child(ITEMS);
        for (DataSnapshot child : itemsSnapshot.getChildren()) {
            Item item = child.getValue(Item.class);
            item.id = child.getKey();
            restaurant.menu.items.put(child.getKey(), item);
        }

        DataSnapshot sectionsSnapshot = menuSnapshot.child(SECTIONS);
        for (DataSnapshot child : sectionsSnapshot.getChildren()) {
            restaurant.menu.sections.add(extractSection(child, restaurant.menu.items));
        }

        DataSnapshot tablesSnapshot = dataSnapshot.child(TABLES);
        for (DataSnapshot child : tablesSnapshot.getChildren()) {
            Table table = extractTable(child);
            restaurant.tables.put(table.id, table);
        }

        return restaurant;
    }

    @NonNull
    private MenuSection extractSection(DataSnapshot dataSnapshot, Map<String, Item> items) {
        MenuSection section = new MenuSection();
        section.headline = dataSnapshot.child(HEADLINE).getValue(String.class);
        String type = dataSnapshot.child(TYPE).getValue(String.class);
        if (type != null)
            section.type = MenuSection.Type.fromString(type);
        section.title = dataSnapshot.child(TITLE).getValue(String.class);
        section.subtitle = dataSnapshot.child(SUBTITLE).getValue(String.class);
        for (DataSnapshot child : dataSnapshot.child(ITEMS).getChildren()) {
            section.items.add(items.get(child.getKey()));
        }
        for (DataSnapshot child : dataSnapshot.child(FEATURED_ITEMS).getChildren()) {
            section.featuredItems.add(items.get(child.getKey()));
        }
        for (DataSnapshot child : dataSnapshot.child(SECTIONS).getChildren()) {
            section.subsections.add(extractSection(child, items));
        }
        return section;
    }

    @NonNull
    private Table extractTable(DataSnapshot dataSnapshot) {
        Table table = new Table();
        table.id = dataSnapshot.getKey();
        table.name = (String) dataSnapshot.child(NAME).getValue();
        table.hasSession = dataSnapshot.hasChild(CURRENT_SESSION);
        return table;
    }

}
