package com.kitchenapi.food;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kitchenapi.apparatus.Apparatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Foods {

    @Getter
    private static final ArrayList<Foods> menu = new ArrayList<>();

    @Getter
    private final int id;

    private final ReentrantLock lock = new ReentrantLock();

    @Getter
    private final String name;

    @Getter
    private final int preparation_time;

    @Getter
    private final int complexity;

    @Getter
    private Apparatus cooking_apparatus;

    private Boolean isPreparing = false;

    public Foods(int id) {
        this.id = id;

        Foods foods = menu.get(id);

        this.name = foods.name;
        this.preparation_time = foods.preparation_time;
        this.complexity = foods.complexity;
        this.cooking_apparatus = foods.cooking_apparatus;

    }

    @JsonCreator
    public Foods(@JsonProperty("id") int id,
                 @JsonProperty("name") String name,
                 @JsonProperty("preparation-time") int preparation_time,
                 @JsonProperty("complexity") int complexity,
                 @JsonProperty("cooking-apparatus") String cooking_apparatus) {
        this.id = id;
        this.name = name;
        this.preparation_time = preparation_time;
        this.complexity = complexity;

        if (cooking_apparatus == null) this.cooking_apparatus = null;
        else
            try {
                this.cooking_apparatus = Apparatus.valueOf(cooking_apparatus);
            } catch (IllegalArgumentException e) {
                System.out.println("Wrong cooking apparatus");
                System.exit(1);
            }

        menu.add(this);

    }

    public void makePreparing() {
        this.isPreparing = true;
    }

    public Boolean isPreparing() {
        return isPreparing;
    }

    public boolean tryLock() {
        return lock.tryLock();
    }

    public void unlock() {
        lock.unlock();
    }
}