package com.example.kitchenapi.cooker;

import com.example.kitchenapi.order.Order;

import java.util.ArrayList;

public class Cooker implements Runnable{

    private static ArrayList<Order> orders;

    private static int count =0;

    private final int id = count++;

    private final int rank;

    private final int proficiency;

    private final String name = "Cook";

    private final String catchPhrase = "Kek";

    public static void putOrders() {
        if (orders==null)
        Cooker.orders = new ArrayList<>();
    }

    public Cooker() {
        rank = (int) (Math.random()*2+1);
        proficiency = rank + (int) (Math.round(Math.random()));
    }

    @Override
    public void run() {

    }
}
