package com.example.kitchenapi.order;

import com.example.kitchenapi.food.Food;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.example.kitchenapi.KitchenApiApplication.timeUnit;

public class Order {

    private int order_id;
    private int table_id;
    private int waiter_id;
    private ArrayList<Integer> items;
    private ArrayList<HashMap<String,Integer>> cooking_details;
    private int priority;
    private int max_wait;
    private int pick_up_time;
    private long cooking_time;

    private long generalPriority;
    private ArrayList<Food> foods = new ArrayList<>();
    private Boolean startedCooking = false;
    private Boolean finished = false;

    public Order() {
        cooking_details = new ArrayList<>();
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
        for (Integer item : items)
            foods.add(new Food(item));
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

    public long getCooking_time() {
        return cooking_time; }

    public ArrayList<HashMap<String, Integer>> getCooking_details() {
        return cooking_details;
    }

    public Boolean tryLock(int index) {
        return foods.get(index).tryLock();
    }

    public void unlock(int index) {
        foods.get(index).unlock();
    }

    public synchronized void cookingFinished() {
        //cooking_time = (long) ((System.currentTimeMillis() - cooking_time) / Math.pow(10,power));
        if (!finished) {
            cooking_time = timeUnit.convert(System.currentTimeMillis() - cooking_time, TimeUnit.MILLISECONDS);
            finished = true;
        }
    }

    public Boolean isDone() {
        return cooking_details.size() == items.size();
    }

    public void makeDone(int index, int cooker_id) {
        HashMap<String, Integer> detail = new HashMap<>();
        detail.put("food_id",items.get(index));
        detail.put("cook_id",cooker_id);
        add(detail);
        System.out.println("Cooker "+cooker_id+" has prepared order "+order_id+" dish "+foods.get(index).getName());
        if (isDone()) cookingFinished();
    }

    public synchronized void add(HashMap<String, Integer> detail) {
        cooking_details.add(detail);
    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    public Boolean startedCooking() {
        return startedCooking;
    }

    public void startCooking() {
        this.cooking_time = System.currentTimeMillis();
        this.startedCooking = true;
    }

    public long getGeneralPriority() {
        return generalPriority;
    }

    public void setGeneralPriority() {
        this.generalPriority = pick_up_time - priority;
    }

    public int getFoodToCook(int rank) {
        int maxComp = 0;
        int index = 0;
        for (int i = 0; i < foods.size(); i++) {
            Food food = foods.get(i);
            if (!food.isLocked() && food.getComplexity()<=rank && maxComp < food.getComplexity() && !food.isPreparing()) {
                maxComp = food.getComplexity();
                index = i;
            }
        }

        return index;
    }

    public Boolean isOccupied() {
        for (Food food : foods)
            if (!food.isPreparing())
                return false;
            return true;
    }

    @Override
    public String toString() {
        return "Order{" +
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
