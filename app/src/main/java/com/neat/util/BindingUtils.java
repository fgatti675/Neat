package com.neat.util;

import android.databinding.BindingAdapter;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.neat.R;
import com.neat.model.Item;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by f.gatti.gomez on 25/10/16.
 */

public class BindingUtils {

    private static Map<String, Integer> iconMap = new HashMap<String, Integer>() {{
        put("apple", R.drawable.item_apple);
        put("apple_alt", R.drawable.item_apple_alt);
        put("apple_alt_3", R.drawable.item_apple_alt_3);
        put("beer_bottle", R.drawable.item_beer_bottle);
        put("beverage", R.drawable.item_beverage);
        put("bottles", R.drawable.item_bottles);
        put("bao_bun", R.drawable.item_bao_bun);
        put("brandy_glass", R.drawable.item_brandy_glass);
        put("burger", R.drawable.item_burger);
        put("burger_alt", R.drawable.item_burger_alt);
        put("burger_drink", R.drawable.item_burger_drink);
        put("butcher_knife", R.drawable.item_butcher_knife);
        put("cake", R.drawable.item_cake);
        put("can", R.drawable.item_can);
        put("carrot", R.drawable.item_carrot);
        put("cheese", R.drawable.item_cheese);
        put("chef", R.drawable.item_chef);
        put("cocktail", R.drawable.item_cocktail);
        put("cocktail_alt_2", R.drawable.item_cocktail_alt_2);
        put("cocktail_alt_3", R.drawable.item_cocktail_alt_3);
        put("cocktail_alt_4", R.drawable.item_cocktail_alt_4);
        put("cocktail_alt_5", R.drawable.item_cocktail_alt_5);
        put("cocktail_alt_6", R.drawable.item_cocktail_alt_6);
        put("cocktail_glasses", R.drawable.item_cocktail_glasses);
        put("coffee", R.drawable.item_coffee);
        put("coffee_alt", R.drawable.item_coffee_alt);
        put("coffee_alt_2", R.drawable.item_coffee_alt_2);
        put("coffee_alt_3", R.drawable.item_coffee_alt_3);
        put("coffee_pot", R.drawable.item_coffee_pot);
        put("coffee_to_go", R.drawable.item_coffee_to_go);
        put("coke_alt", R.drawable.item_coke_alt);
        put("coke_bottle", R.drawable.item_coke_bottle);
        put("cooking_pot", R.drawable.item_cooking_pot);
        put("croissant", R.drawable.item_croissant);
        put("cutlery", R.drawable.item_cutlery);
        put("dessert", R.drawable.item_dessert);
        put("dessert_alt", R.drawable.item_dessert_alt);
        put("ding", R.drawable.item_ding);
        put("dish", R.drawable.item_dish);
        put("duck", R.drawable.item_duck);
        put("eggs", R.drawable.item_eggs);
        put("farfalle", R.drawable.item_farfalle);
        put("fill", R.drawable.item_fill);
        put("fork", R.drawable.item_fork);
        put("fusilli", R.drawable.item_fusilli);
        put("glass_small", R.drawable.item_glass_small);
        put("glass_small_alt", R.drawable.item_glass_small_alt);
        put("gnocchi", R.drawable.item_gnocchi);
        put("hamburger", R.drawable.item_hamburger);
        put("hot_cup", R.drawable.item_hot_cup);
        put("ice_cream", R.drawable.item_ice_cream);
        put("lasagna", R.drawable.item_lasagna);
        put("meatballs", R.drawable.item_meatballs);
        put("milk_brick", R.drawable.item_milk_brick);
        put("muffin", R.drawable.item_muffin);
        put("muffin_alt", R.drawable.item_muffin_alt);
        put("muffin_alt_2", R.drawable.item_muffin_alt_2);
        put("noodles", R.drawable.item_noodles);
        put("octopus", R.drawable.item_octopus);
        put("pear", R.drawable.item_pear);
        put("penne", R.drawable.item_penne);
        put("pizza_alt", R.drawable.item_pizza_alt);
        put("pizza_full", R.drawable.item_pizza_full);
        put("plastic_bottle", R.drawable.item_plastic_bottle);
        put("plastic_bottle_alt", R.drawable.item_plastic_bottle_alt);
        put("ravioli", R.drawable.item_ravioli);
        put("roll", R.drawable.item_roll);
        put("salt", R.drawable.item_salt);
        put("sausage", R.drawable.item_sausage);
        put("shake", R.drawable.item_shake);
        put("shrimp", R.drawable.item_shrimp);
        put("spoon", R.drawable.item_spoon);
        put("steak", R.drawable.item_steak);
        put("steak_alt", R.drawable.item_steak_alt);
        put("taco", R.drawable.item_taco);
        put("tee", R.drawable.item_tee);
        put("tee_alt", R.drawable.item_tee_alt);
        put("water_bottle", R.drawable.item_water_bottle);
        put("wine_glass", R.drawable.item_wine_glass);
        put("wine_glass_alt", R.drawable.item_wine_glass_alt);
        put("wine_glasses", R.drawable.item_wine_glasses);
        put("wine_rack", R.drawable.item_wine_rack);
    }};

    @BindingAdapter({"itemIcon"})
    public static void bindIconImage(ImageView view, Item item) {
        view.setImageResource(getIconDrawableRes(item));
    }

    @DrawableRes
    public static Integer getIconDrawableRes(Item item) {
        Integer res = iconMap.get(item.icon);
        if (res == null) res = R.drawable.item_fork;
        return res;
    }

    @BindingAdapter({"itemPrice"})
    public static void bindItemPriceText(TextView view, Item item) {
        String formattedPrice = null;
        if (item.currency.equals("EUR"))
            formattedPrice = String.format(Locale.getDefault(), "%.2f€", item.price);
        else
            formattedPrice = String.format(Locale.getDefault(), "%.2f %s", item.price, item.currency);
        view.setText(formattedPrice);
    }

    @BindingAdapter({"orderItem", "orderCount"})
    public static void bindOrderPriceText(TextView view, Item item, int count) {
        String formattedPrice = null;
        float totalPrice = count * item.price;
        if (item.currency.equals("EUR")) {
            formattedPrice = String.format(Locale.getDefault(), "%.2f€", totalPrice);
        } else {
            formattedPrice = String.format(Locale.getDefault(), "%.2f %s", totalPrice, item.currency);
        }
        view.setText(formattedPrice);
    }

    @BindingAdapter({"itemImage"})
    public static void bindImage(ImageView view, Item item) {
        if (item.imageUrl != null) {
            Glide.with(view.getContext())
                    .load(item.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .into(view);
        } else {
            //TODO
        }
    }

    @BindingAdapter({"itemImageDontTransform"})
    public static void bindImageDontTransform(ImageView view, Item item) {
        if (item.imageUrl != null) {
            Glide.with(view.getContext())
                    .load(item.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view);
        } else {
            //TODO
        }
    }
}
