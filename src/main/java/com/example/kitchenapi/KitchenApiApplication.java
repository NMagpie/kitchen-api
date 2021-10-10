package com.example.kitchenapi;

import com.example.kitchenapi.order.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class KitchenApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KitchenApiApplication.class, args);



	}

}
