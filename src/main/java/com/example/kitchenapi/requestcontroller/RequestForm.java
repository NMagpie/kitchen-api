package com.example.kitchenapi.requestcontroller;

import java.util.ArrayList;

public class RequestForm {

    private int order_id;
    private int table_id;
    private int waiter_id;
    private ArrayList<Integer> items;
    private int priority;
    private int max_wait;
    private int pick_up_time;

    public RequestForm() {
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public int getWaiter_id() {
        return waiter_id;
    }

    public void setWaiter_id(int waiter_id) {
        this.waiter_id = waiter_id;
    }

    public ArrayList<Integer> getItems() {
        return items;
    }

    public void setItems(ArrayList<Integer> items) {
        this.items = items;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getMax_wait() {
        return max_wait;
    }

    public void setMax_wait(int max_wait) {
        this.max_wait = max_wait;
    }

    public int getPick_up_time() {
        return pick_up_time;
    }

    public void setPick_up_time(int pick_up_time) {
        this.pick_up_time = pick_up_time;
    }

    @Override
    public String toString() {
        return "RequestForm{" +
                "order_id=" + order_id +
                ", table_id=" + table_id +
                ", waiter_id=" + waiter_id +
                ", items=" + items +
                ", priority=" + priority +
                ", max_wait=" + max_wait +
                ", pick_up_time=" + pick_up_time +
                '}';
    }
}
