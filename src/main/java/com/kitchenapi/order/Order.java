package com.kitchenapi.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kitchenapi.food.Food;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.kitchenapi.KitchenApiApplication.getTimeUnit;

@JsonIgnoreProperties({"foods", "generalPriority", "startedCooking", "finished"})
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
    private final int pick_up_time;
    @JsonProperty
    private final ArrayList<HashMap<String, Integer>> cooking_details;
    @Getter
    private final ArrayList<Food> foods = new ArrayList<>();
    @Getter
    private final long generalPriority;
    @JsonProperty
    @Getter
    private long cooking_time;
    @Getter
    private Boolean startedCooking = false;
    private Boolean finished = false;

    @JsonCreator
    public Order(@JsonProperty("order_id")     int order_id,
                 @JsonProperty("table_id")     int table_id,
                 @JsonProperty("waiter_id")    int waiter_id,
                 @JsonProperty("items")        ArrayList<Integer> items,
                 @JsonProperty("priority")     int priority,
                 @JsonProperty("max_wait")     double max_wait,
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
            foods.add(new Food(item));

        this.generalPriority = pick_up_time - priority;
    }

    public synchronized void cookingFinished() {
        if (!finished) {
            cooking_time = getTimeUnit().convert(System.currentTimeMillis() - cooking_time, TimeUnit.MILLISECONDS);
            finished = true;
        }
    }

    @JsonIgnore
    public Boolean isDone() {
        return cooking_details.size() == items.size();
    }

    public void makeDone(Food food, int cooker_id) {
        HashMap<String, Integer> detail = new HashMap<>();
        detail.put("food_id", food.getId());
        detail.put("cook_id", cooker_id);
        add(detail);
        System.out.println("Cooker " + cooker_id + " has prepared order " + order_id + " dish " + food.getName());
        if (isDone()) cookingFinished();
    }

    public synchronized void add(HashMap<String, Integer> detail) {
        cooking_details.add(detail);
    }

    public Boolean startedCooking() {
        return startedCooking;
    }

    public void startCooking() {
        this.cooking_time = System.currentTimeMillis();
        this.startedCooking = true;
    }

    public Food getFoodToCook(int rank) {
        int maxComp = 0;
        Food foodToCook = null;
        for (Food food : foods) {
            if (!food.isPreparing() &&
                    food.getComplexity() <= rank &&
                    maxComp < food.getComplexity() &&
                    food.tryLock()
            ) {
                if (foodToCook != null) foodToCook.unlock();
                maxComp = food.getComplexity();
                foodToCook = food;
            }
        }
        if (foodToCook != null)
            foodToCook.makePreparing();
        return foodToCook;
    }

    @JsonIgnore
    public Boolean isOccupied() {
        return foods.stream().allMatch(Food::isPreparing);
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
