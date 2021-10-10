package com.example.kitchenapi.food;

public class Food {

    private int id;

    private String name;

    private int preparation_time;

    private int complexity;

    private String cooking_apparatus;

    public Food(int id) {
        this.id = id;

        switch (id){
            case (1):
                this.name = "pizza";
                this.preparation_time = 20;
                this.complexity = 2;
                this.cooking_apparatus = "oven";
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
                this.cooking_apparatus = "stove";
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
                this.cooking_apparatus = "oven";
                break;

            case (6):
                this.name = "Waffles";
                this.preparation_time = 10;
                this.complexity = 1;
                this.cooking_apparatus = "stove";
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
                this.cooking_apparatus = "oven";
                break;

            case (9):
                this.name = "Burger";
                this.preparation_time = 15;
                this.complexity = 1;
                this.cooking_apparatus = "oven";
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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPreparation_time() {
        return preparation_time;
    }

    public int getComplexity() {
        return complexity;
    }

    public String getCooking_apparatus() {
        return cooking_apparatus;
    }

}