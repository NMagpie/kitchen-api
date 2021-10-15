package com.example.kitchenapi;

import com.example.kitchenapi.apparatus.Apparatus;
import com.example.kitchenapi.cooker.Cooker;
import com.example.kitchenapi.order.Order;
import com.example.kitchenapi.requestcontroller.RequestController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
public class KitchenApiApplication {

	//timeUnit only greater, than NANOSECONDS
	public static final TimeUnit timeUnit = TimeUnit.SECONDS;

	public static final TimeUnit restTime = TimeUnit.values()[timeUnit.ordinal()-1];

	public static final ArrayList<Order> orders = new ArrayList<>();

	public static final ArrayList<Apparatus> apparatuses = new ArrayList<>();

	public static void main(String[] args) {

		if (timeUnit.ordinal()<1) {
			System.out.println("Wrong timeUnit input. Exiting program...");
			System.exit(0);
		}

		apparatuses.add(Apparatus.Stove);
		apparatuses.add(Apparatus.Oven);

		ReentrantLock lock = new ReentrantLock();
		Condition condition = lock.newCondition();

		ArrayList<Cooker> cookers = new ArrayList<>();

/*		for (int i = 0; i < 5; i++) {
			cookers.add(new Cooker(condition));
			new Thread(cookers.get(i)).start();
		}*/

		cookers.add(new Cooker(1,condition));
		new Thread(cookers.get(0)).start();

		cookers.add(new Cooker(1,condition));
		new Thread(cookers.get(1)).start();

		cookers.add(new Cooker(2,condition));
		new Thread(cookers.get(2)).start();

		cookers.add(new Cooker(2,condition));
		new Thread(cookers.get(3)).start();

		cookers.add(new Cooker(2,condition));
		new Thread(cookers.get(4)).start();

		cookers.add(new Cooker(3,condition));
		new Thread(cookers.get(5)).start();

		RequestController.setCondition(condition);

		SpringApplication.run(KitchenApiApplication.class, args);

	}

}
