package com.neat.model.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.gatti.gomez on 10/10/16.
 */

public class MenuSection implements Serializable {

    public enum Type {
        small, featured, list;

        public static Type fromString(String s){
            try {
                return valueOf(s);
            } catch (IllegalArgumentException e) {
                return list;
            }
        }
    }

    public String headline;

    public String title;

    public String subtitle;

    public Type type;

    public List<MenuSection> subsections = new ArrayList<>();

    public List<Item> items = new ArrayList<>();

    public List<Item> featuredItems = new ArrayList<>();

}
