package com.example.kitchenapi.requestcontroller;

import com.example.kitchenapi.KitchenApiApplication;
import com.example.kitchenapi.order.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Condition;

@RestController
public class RequestController {

    private static Condition condition;

    public static void setCondition(Condition condition) {
        RequestController.condition = condition;
    }

    @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getOrder(@RequestBody Order object) {
        object.setGeneralPriority();
        KitchenApiApplication.orders.add(object);
        //condition.signalAll();
        return "Success!";
    }

}
