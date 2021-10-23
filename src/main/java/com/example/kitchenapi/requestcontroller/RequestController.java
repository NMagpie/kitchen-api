package com.example.kitchenapi.requestcontroller;

import com.example.kitchenapi.KitchenApiApplication;
import com.example.kitchenapi.order.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getOrder(@RequestBody Order object) {
        object.setGeneralPriority();
        KitchenApiApplication.orders.add(object);
        return "Success!";
    }

}