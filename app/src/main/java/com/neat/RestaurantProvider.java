package com.neat;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neat.model.Item;
import com.neat.model.Menu;
import com.neat.model.MenuSection;
import com.neat.model.Restaurant;

import java.util.Map;


/**
 * Created by f.gatti.gomez on 10/10/16.
 */
public class RestaurantProvider {

    public interface Callback {
        void onRestaurantLoaded(Restaurant restaurant);
    }

    private static final String TAG = "RestaurantProvider";

    public void getRestaurant(final String restaurantSlug, final Callback callback) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference restaurantRef = database.child("restaurants").child(restaurantSlug);
        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant restaurant = extractRestaurant(dataSnapshot);
                callback.onRestaurantLoaded(restaurant);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }

    @NonNull
    private Restaurant extractRestaurant(DataSnapshot dataSnapshot) {
        Restaurant restaurant = new Restaurant();
        restaurant.title = dataSnapshot.child("title").getValue(String.class);
        restaurant.subtitle = dataSnapshot.child("subtitle").getValue(String.class);
        restaurant.headerUrl = dataSnapshot.child("headerUrl").getValue(String.class);
        restaurant.slug = dataSnapshot.getKey();

        restaurant.menu = new Menu();
        DataSnapshot menuSnapshot = dataSnapshot.child("menu");
        restaurant.menu.currency = menuSnapshot.child("currency").getValue(String.class);
        DataSnapshot itemsSnapshot = menuSnapshot.child("items");
        for (DataSnapshot child : itemsSnapshot.getChildren()) {
            Item item = child.getValue(Item.class);
            item.id = child.getKey();
            restaurant.menu.items.put(child.getKey(), item);
        }

        DataSnapshot sectionsSnapshot = menuSnapshot.child("sections");
        for (DataSnapshot child : sectionsSnapshot.getChildren()) {
            restaurant.menu.sections.add(extractSection(child, restaurant.menu.items));
        }
        return restaurant;
    }

    @NonNull
    private MenuSection extractSection(DataSnapshot dataSnapshot, Map<String, Item> items) {
        MenuSection section = new MenuSection();
        section.headline = dataSnapshot.child("headline").getValue(String.class);
        String type = dataSnapshot.child("type").getValue(String.class);
        if (type != null)
            section.type = MenuSection.Type.fromString(type);
        section.title = dataSnapshot.child("title").getValue(String.class);
        section.subtitle = dataSnapshot.child("subtitle").getValue(String.class);
        for (DataSnapshot child : dataSnapshot.child("items").getChildren()) {
            section.items.add(items.get(child.getKey()));
        }
        for (DataSnapshot child : dataSnapshot.child("featuredItems").getChildren()) {
            section.featuredItems.add(items.get(child.getKey()));
        }
        for (DataSnapshot child : dataSnapshot.child("sections").getChildren()) {
            section.subsections.add(extractSection(child, items));
        }
        return section;
    }


}
