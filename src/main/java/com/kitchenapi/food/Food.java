package com.kitchenapi.food;

import com.kitchenapi.apparatus.Apparatus;
import lombok.Getter;

import java.util.concurrent.locks.ReentrantLock;

public class Food {

    @Getter
    private final int id;

    private final ReentrantLock lock = new ReentrantLock();

    @Getter
    private String name;

    @Getter
    private int preparation_time;

    @Getter
    private int complexity;

    @Getter
    private Apparatus cooking_apparatus;

    private Boolean isPreparing = false;

    public Food(int id) {
        this.id = id;

        switch (id) {
            case (1):
                this.name = "pizza";
                this.preparation_time = 20;
                this.complexity = 2;
                this.cooking_apparatus = Apparatus.Oven;
                break;

            case (2):
                this.name = "salad";
                this.preparation_time = 10;
                this.complexity = 1;
                this.cooking_apparatus = null;
                break;

            case (3):
                this.name = "zeama";
                this.preparation_time = 7;
                this.complexity = 1;
                this.cooking_apparatus = Apparatus.Stove;
                break;

            case (4):
                this.name = "Scallop Sashimi with Meyer Lemon Confit";
                this.preparation_time = 32;
                this.complexity = 3;
                this.cooking_apparatus = null;
                break;

            case (5):
                this.name = "Island Duck with Mulberry Mustard";
                this.preparation_time = 35;
                this.complexity = 3;
                this.cooking_apparatus = Apparatus.Oven;
                break;

            case (6):
                this.name = "Waffles";
                this.preparation_time = 10;
                this.complexity = 1;
                this.cooking_apparatus = Apparatus.Stove;
                break;

            case (7):
                this.name = "Aubergine";
                this.preparation_time = 20;
                this.complexity = 2;
                this.cooking_apparatus = null;
                break;

            case (8):
                this.name = "Lasagna";
                this.preparation_time = 30;
                this.complexity = 2;
                this.cooking_apparatus = Apparatus.Oven;
                break;

            case (9):
                this.name = "Burger";
                this.preparation_time = 15;
                this.complexity = 1;
                this.cooking_apparatus = Apparatus.Oven;
                break;

            case (10):
                this.name = "Gyros";
                this.preparation_time = 15;
                this.complexity = 1;
                this.cooking_apparatus = null;
                break;
            default:
                System.out.println("Wrong id");
                break;
        }

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