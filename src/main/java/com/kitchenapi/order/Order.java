package com.kitchenapi.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kitchenapi.food.Foods;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.kitchenapi.KitchenApiApplication.*;

@JsonIgnoreProperties({"foods", "generalPriority", "startedCooking", "finished", "lock"})
public class Order {

    @JsonProperty
    @Getter
    private final int order_id;

    @JsonProperty
    private final int table_id;

    @JsonProperty
    private final int waiter_id;

    @JsonProperty
    private final ArrayList<Integer> items;

    @JsonProperty
    private final int priority;

    @JsonProperty
    private final double max_wait;

    @JsonProperty
    private final long pick_up_time;

    @JsonProperty
    private final ArrayList<HashMap<String, Integer>> cooking_details;

    @Getter
    private final ArrayList<Foods> foods = new ArrayList<>();

    @Getter
    private final long generalPriority;
    private final ReentrantLock lock = new ReentrantLock();
    @JsonProperty
    @Getter
    private long cooking_time;
    @Getter
    private Boolean startedCooking = false;
    @Getter
    private Boolean finished = false;

    @JsonCreator
    public Order(@JsonProperty("order_id") int order_id,
                 @JsonProperty("table_id") int table_id,
                 @JsonProperty("waiter_id") int waiter_id,
                 @JsonProperty("items") ArrayList<Integer> items,
                 @JsonProperty("priority") int priority,
                 @JsonProperty("max_wait") double max_wait,
                 @JsonProperty("pick_up_time") int pick_up_time) {

        this.order_id = order_id;
        this.table_id = table_id;
        this.waiter_id = waiter_id;
        this.items = items;
        this.priority = priority;
        this.max_wait = max_wait;
        this.pick_up_time = pick_up_time;
        this.cooking_details = new ArrayList<>();

        for (Integer item : items)
            foods.add(new Foods(item));

        this.generalPriority = pick_up_time - priority;
    }

    public synchronized void cookingFinished() {
        if (!finished) {
            cooking_time = getTimeUnit().convert(System.currentTimeMillis() - cooking_time, TimeUnit.MILLISECONDS);
            finished = true;
        }
    }

    @JsonIgnore
    private Boolean isDone() {
        return cooking_details.size() == items.size();
    }

    public void makeDone(Foods foods, int cooker_id) {
        HashMap<String, Integer> detail = new HashMap<>();
        detail.put("food_id", foods.getId());
        detail.put("cook_id", cooker_id);
        add(detail);
        System.out.println("Cooker " + cooker_id + " has prepared order " + order_id + " dish " + foods.getName());
        if (isDone()) cookingFinished();
    }

    public synchronized void add(HashMap<String, Integer> detail) {
        cooking_details.add(detail);
    }

    public boolean wrapForOrder() {
        if (finished && lock.tryLock() && orders.contains(this)) {
            orders.remove(this);
            lock.unlock();
            return true;
        }
        return false;
    }

    public Boolean startedCooking() {
        return startedCooking;
    }

    public void startCooking() {
        this.cooking_time = System.currentTimeMillis();
        this.startedCooking = true;
    }

    public Foods getFoodToCook(int rank) {

        int maxComp = 0;

        Foods foodsToCook = null;

        for (Foods foods : this.foods)
            if (!foods.isPreparing() &&
                    foods.getComplexity() <= rank &&
                    maxComp < foods.getComplexity() &&
                    foods.tryLock() &&
                    isApparatusFree(foods.getCooking_apparatus())) {

                if (foodsToCook != null) foodsToCook.unlock();
                maxComp = foods.getComplexity();
                foodsToCook = foods;

            }

        if (foodsToCook != null) foodsToCook.makePreparing();

        return foodsToCook;
    }

    @JsonIgnore
    public Boolean isOccupied(int rank) {
        return foods.stream().noneMatch(foods -> !foods.isPreparing() && foods.getComplexity() <= rank && isApparatusFree(foods.getCooking_apparatus()));
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
