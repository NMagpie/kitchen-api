package com.example.kitchenapi.order;

import java.util.ArrayList;

public class Order {

    private static int count = 0;

    private final int id = count++;

    private ArrayList<Integer> items;

    private final int priority = 0;

    private double max_wait = 0;

    private long pickupTime;

    public Order() {

    }

    public long getPickupTime() {
        return pickupTime;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getItems() {
        return items;
    }

    public int getPriority() {
        return priority;
    }

    public double getMax_wait() {
        return max_wait;
    }
}