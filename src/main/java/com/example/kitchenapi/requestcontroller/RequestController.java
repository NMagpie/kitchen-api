package com.example.kitchenapi.requestcontroller;

import com.example.kitchenapi.KitchenApiApplication;
import com.example.kitchenapi.order.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/order")
public class RequestController {

    @PostMapping(value = "/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String getOrder(@RequestBody Order object) {
        object.setGeneralPriority();
        KitchenApiApplication.orders.add(object);
        return "Success!";
    }

    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String test(@RequestBody Order/*String*/ object) {
        return "Success!";
    }

}