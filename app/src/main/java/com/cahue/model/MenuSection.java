package com.cahue.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.gatti.gomez on 10/10/16.
 */

public class MenuSection implements Serializable {

    public enum Type {
        TAGS, FEATURED, LIST
    }

    public String headline;

    public String title;

    public String subtitle;

    public Type type;

    public List<MenuSection> subsections = new ArrayList<>();

    public List<Item> items = new ArrayList<>();

    /**
     * Get all best selling items recursively
     *
     * @return
     */
    public List<Item> getBestSellingItems() {
        List<Item> featuredItems = new ArrayList<>();
        for (Item item : items) if (item.bestSelling) featuredItems.add(item);
        for (MenuSection subsection : subsections)
            featuredItems.addAll(subsection.getBestSellingItems());
        return featuredItems;
    }

}
