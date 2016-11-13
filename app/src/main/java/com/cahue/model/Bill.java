package com.cahue.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */

public class Bill implements Serializable {

    public List<Order> orders = new ArrayList<>();
}
