package com.kitchenapi.cooker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchenapi.KitchenApiApplication;
import com.kitchenapi.apparatus.Apparatus;
import com.kitchenapi.food.Food;
import com.kitchenapi.order.Order;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static com.kitchenapi.KitchenApiApplication.acquireApparatus;
import static com.kitchenapi.KitchenApiApplication.releaseApparatus;

public class Cooker extends Thread {

    private static final String url = KitchenApiApplication.getURL() + "/distribution";
    private static final HttpHeaders headers = new HttpHeaders() {{
        setContentType(MediaType.APPLICATION_JSON);
    }};
    private static int count = 0;
    private final int id = count++;
    private final int rank;
    private final String name = "Cook";
    private final String catchPhrase = "Kek";
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();
    private int proficiency;

    public Cooker() {
        int a = 0;
        this.rank = (int) (Math.random() * 3 + 1);
        if (Math.random() > 0.7) a++;
        this.proficiency = rank + a;
        //this.proficiency = rank + (int) (Math.round(Math.random()));
    }

    public Cooker(int rank) {
        int a = 0;
        this.rank = rank;
        if (Math.random() > 0.7) a++;
        this.proficiency = rank + a;
    }

    public static synchronized void noResponse() {
        System.out.println("No response! Exiting program...");
        System.exit(0);
    }

    public void sendOrder(Order order) {

        ObjectMapper mapper = new ObjectMapper();

        String json = null;

        try {
            json = mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpEntity<String> request = new HttpEntity<>(json, headers);
        try {
            restTemplate.postForObject(url, request, String.class);
        } catch (ResourceAccessException e) {
            noResponse();
        }

    }

    private Order getMaxPriority() {

        Order orderReturn = null;

        long priority = Long.MAX_VALUE;

        for (int i = 0; i < KitchenApiApplication.orders.size(); i++) {

            Order order = KitchenApiApplication.orders.get(i);

            if (order != null && !order.isOccupied(rank) && priority > order.getGeneralPriority()) {
                priority = order.getGeneralPriority();
                orderReturn = order;
            }

        }

        return orderReturn;
    }

    private void waitRest() {

        try {
            KitchenApiApplication.getRestTime().sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        Order order;

        Food food;

        Thread.currentThread().setName("Cooker-" + id);

        while (true) {

            if (!KitchenApiApplication.orders.isEmpty() && (order = getMaxPriority()) != null && proficiency > 0) {

                food = order.getFoodToCook(rank);

                if (food != null) {
                    System.out.println("Cooker " + id + " is preparing order " + order.getOrder_id() + " dish " + food.getName());

                    if (!order.startedCooking()) order.startCooking();

                    proficiency--;

                    new Thread(new Cooking(order, food)).start();

                    food.unlock();
                }

            }

            waitRest();

        }

    }

    @Override
    public String toString() {
        return "Cooker{" +
                "id=" + id +
                ", rank=" + rank +
                ", proficiency=" + proficiency +
                ", name='" + name + '\'' +
                '}';
    }

    private class Cooking implements Runnable {

        private final Order order;

        private final Food food;

        public Cooking(Order order, Food food) {
            this.order = order;
            this.food = food;
        }

        @Override
        public void run() {

            Thread.currentThread().setName("Cooking-" + id);

            Apparatus apparatus = food.getCooking_apparatus();

            acquireApparatus(apparatus);

            try {
                KitchenApiApplication.getTimeUnit().sleep(food.getPreparation_time());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            releaseApparatus(apparatus);

            order.makeDone(food, id);

            proficiency++;

            if (order.wrapForOrder()) {

                sendOrder(order);

                System.out.println("Order " + order.getOrder_id() + " was prepared; sent back within " + order.getCooking_time() + " " + KitchenApiApplication.getTimeUnit().name() + "\n");
            }
        }
    }
}