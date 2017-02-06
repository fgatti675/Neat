package com.neat.model.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by f.gatti.gomez on 10/10/16.
 */

public class Menu implements Serializable {

    public String currency;

    public Map<String, Item> items = new HashMap<>();

    public List<MenuSection> sections = new ArrayList<>();

}
