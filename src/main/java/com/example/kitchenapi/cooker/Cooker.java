package com.example.kitchenapi.cooker;

import com.example.kitchenapi.order.Order;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.locks.Condition;

import static com.example.kitchenapi.KitchenApiApplication.*;

public class Cooker implements Runnable{

    private static int count = 0;

    private final int id = count++;

    private final int rank;

    private int proficiency;

    private final String name = "Cook";

    private final String catchPhrase = "Kek";

    private final Condition condition;

    private static final String url = "http://localhost:8081/distribution";

    private final RestTemplate restTemplate = new RestTemplate();

    private static final HttpHeaders headers = new HttpHeaders() {{setContentType(MediaType.APPLICATION_JSON);}};

    public Cooker(Condition condition) {
        int a = 0;
        this.rank = (int) (Math.random()*2+1);
        if (Math.random() > 0.7) a++;
        this.proficiency = rank + a;
        this.condition = condition;
        //this.proficiency = rank + (int) (Math.round(Math.random()));
    }

    public Cooker(int rank, Condition condition) {
        int a = 0;
        this.rank = rank;
        if (Math.random() > 0.7) a++;
        this.proficiency = rank + a;
        this.condition = condition;
    }

    public void sendOrder(Order order) {
        //RestTemplate restTemplate = new RestTemplate();
        JSONObject object = new JSONObject(order);

        object.remove("generalPriority");
        object.remove("foods");
        object.remove("startedCooking");
        object.remove("occupied");
        object.remove("done");

        HttpEntity<String> request = new HttpEntity<>(object.toString(),headers);
        String response = restTemplate.postForObject(url,request,String.class);
        if (response == null || !response.equals("Success!")) {
            System.out.println("No response! Exiting program...");
            System.exit(0);
        }

        //System.out.println(object);

    }

    private int getMaxPriority() {
        int index = -1;
        long priority = Long.MAX_VALUE;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (!order.isOccupied() && priority > order.getGeneralPriority() && !order.isDone()) {
                priority = order.getGeneralPriority();
                index = i;
            }
        }
        return index;
    }

    private void waitForOrders() {
        try {
        while (orders.isEmpty())
                condition.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitRest() {
        try {
        restTime.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        int order;

        int food;

        Thread.currentThread().setName("Cooker-"+id);

        while (true) {

            //waitForOrders();

            if (!orders.isEmpty() && (order = getMaxPriority()) != -1 && proficiency > 0) {

                food = orders.get(order).getFoodToCook(rank);

                if (!orders.get(order).getFoods().get(food).isPreparing() && orders.get(order).getFoods().get(food).tryLock()) {
                    orders.get(order).getFoods().get(food).makePreparing();
                    System.out.println("Cooker "+ id + " is preparing order "+orders.get(order).getOrder_id()+ " dish "+ orders.get(order).getFoods().get(food).getName());
                    if (!orders.get(order).startedCooking()) orders.get(order).startCooking();
                    proficiency--;
                    new Thread(new Cooking(order,food)).start();
                    orders.get(order).getFoods().get(food).unlock();
                }

            }

            waitRest();

        }

    }

    private class Cooking implements Runnable {

        private final int orderInd;

        private final int food;

        public Cooking(int order, int food) {
            this.orderInd = order;
            this.food = food;
        }

        @Override
        public void run() {
            Order order = orders.get(orderInd);
            Thread.currentThread().setName("Cooking-"+id);
            try {
                timeUnit.sleep(order.getFoods().get(food).getPreparation_time());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            order.makeDone(food,id);
            proficiency++;
            if (!orders.isEmpty() && order.isDone() && orders.contains(order)) {
                orders.remove(order);
                sendOrder(order);
                System.out.println("Order "+ order.getOrder_id() + " was prepared sent back within "+ order.getCooking_time() + " " + timeUnit.name()+"\n");
            }
        }
    }
}
