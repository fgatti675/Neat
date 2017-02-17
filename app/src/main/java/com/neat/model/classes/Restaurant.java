package com.neat.model.classes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by f.gatti.gomez on 08/11/2016.
 */
public class Restaurant {

    public String id;

    public String title;

    public String headerUrl;

    public String subtitle;

    public Menu menu;

    public Map<String,Table> tables = new HashMap<>();

}
