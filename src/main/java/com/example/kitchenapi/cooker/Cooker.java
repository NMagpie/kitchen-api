package com.example.kitchenapi.cooker;

import com.example.kitchenapi.apparatus.Apparatus;
import com.example.kitchenapi.order.Order;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Semaphore;

import static com.example.kitchenapi.KitchenApiApplication.*;

public class Cooker implements Runnable{

    private static int count = 0;

    private final int id = count++;

    private final int rank;

    private int proficiency;

    private final String name = "Cook";

    private final String catchPhrase = "Kek";

    private final Semaphore ovens;

    private final Semaphore stoves;

    private static final String url = getURL()+"/distribution";

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    private static final HttpHeaders headers = new HttpHeaders() {{setContentType(MediaType.APPLICATION_JSON);}};

    public Cooker() {
        int a = 0;
        this.rank = (int) (Math.random()*2+1);
        if (Math.random() > 0.7) a++;
        this.proficiency = rank + a;
        this.ovens = getOvens();
        this.stoves = getStoves();
        //this.proficiency = rank + (int) (Math.round(Math.random()));
    }

    public Cooker(int rank) {
        int a = 0;
        this.rank = rank;
        if (Math.random() > 0.7) a++;
        this.proficiency = rank + a;
        this.ovens = getOvens();
        this.stoves = getStoves();
    }

    public void sendOrder(Order order) {
        JSONObject object = new JSONObject(order);

        object.remove("generalPriority");
        object.remove("foods");
        object.remove("startedCooking");
        object.remove("occupied");
        object.remove("done");

        HttpEntity<String> request = new HttpEntity<>(object.toString(),headers);
        try {
            restTemplate.postForObject(url,request,String.class);
        } catch (ResourceAccessException e) {
            noResponse();
        }

    }

    public static synchronized void noResponse() {
        System.out.println("No response! Exiting program...");
        System.exit(0);
    }

    private Order getMaxPriority() {
        Order orderReturn = null;
        long priority = Long.MAX_VALUE;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order != null && !order.isOccupied() && priority > order.getGeneralPriority() && !order.isDone()) {
                priority = order.getGeneralPriority();
                orderReturn = order;
            }
        }
        return orderReturn;
    }

    private void waitRest() {
        try {
        getRestTime().sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        Order order;

        int food;

        Thread.currentThread().setName("Cooker-"+id);

        while (true) {

            if (!orders.isEmpty() && (order = getMaxPriority()) != null && proficiency > 0) {

                food = order.getFoodToCook(rank);

                if (!order.getFoods().get(food).isPreparing() && order.getFoods().get(food).tryLock()) {
                    order.getFoods().get(food).makePreparing();
                    System.out.println("Cooker "+ id + " is preparing order "+order.getOrder_id()+ " dish "+ order.getFoods().get(food).getName());
                    if (!order.startedCooking()) order.startCooking();
                    proficiency--;
                    new Thread(new Cooking(order.getOrder_id(),food)).start();
                    order.getFoods().get(food).unlock();
                }

            }

            waitRest();

        }

    }

    private class Cooking implements Runnable {

        private final int orderId;

        private final int food;

        public Cooking(int order, int food) {
            this.orderId = order;
            this.food = food;
        }

        @Override
        public void run() {
            Order order = null;
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i) !=null && orders.get(i).getOrder_id() == orderId) order = orders.get(i);
            }

                if (order == null) return;

            Apparatus apparatus = order.getFoods().get(food).getCooking_apparatus();

            if (apparatus != null)
                if (apparatus.ordinal() == 0) {
                    try {
                        ovens.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        stoves.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            Thread.currentThread().setName("Cooking-"+id);
            try {
                getTimeUnit().sleep(order.getFoods().get(food).getPreparation_time());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            order.makeDone(food,id);
            proficiency++;

            if (apparatus != null)
                if (apparatus.ordinal() == 0) ovens.release();
                else stoves.release();

            if (!orders.isEmpty() && order.isDone() && orders.contains(order)) {
                orders.remove(order);
                sendOrder(order);
                System.out.println("Order "+ order.getOrder_id() + " was prepared sent back within "+ order.getCooking_time() + " " + getTimeUnit().name()+"\n");
            }
        }
    }
}