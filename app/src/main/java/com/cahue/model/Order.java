package com.cahue.model;

import java.io.Serializable;

/**
 * Created by f.gatti.gomez on 09/10/16.
 */

public class Order implements Serializable {

    public enum Status implements Serializable{
        pending, requested, served
    }

    public Item item;

    public int count;

    public Status status;

}
