package com.example.kitchenapi;

import com.example.kitchenapi.apparatus.Apparatus;
import com.example.kitchenapi.cooker.Cooker;
import com.example.kitchenapi.order.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class KitchenApiApplication {

	//timeUnit only greater, than NANOSECONDS
	public static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;

	public static final TimeUnit restTime = TimeUnit.values()[timeUnit.ordinal()-1];

	public static final ArrayList<Order> orders = new ArrayList<>();

	private static Semaphore stoves;

	private static Semaphore ovens;

	public static void main(String[] args) {

		if (timeUnit.ordinal()<1) {
			System.out.println("Wrong timeUnit input. Exiting program...");
			System.exit(0);
		}

		int cookersSize = 6;

		stoves = new Semaphore((int) Math.round(cookersSize / 1.75),true);

		ovens = new Semaphore((int) Math.round(cookersSize / 3.5),true);

		SpringApplication.run(KitchenApiApplication.class, args);

		ArrayList<Cooker> cookers = new ArrayList<>();

/*		for (int i = 0; i < 5; i++) {
			cookers.add(new Cooker(condition));
			new Thread(cookers.get(i)).start();
		}*/

		cookers.add(new Cooker(1));
		new Thread(cookers.get(0)).start();

		cookers.add(new Cooker(1));
		new Thread(cookers.get(1)).start();

		cookers.add(new Cooker(2));
		new Thread(cookers.get(2)).start();

		cookers.add(new Cooker(2));
		new Thread(cookers.get(3)).start();

		cookers.add(new Cooker(2));
		new Thread(cookers.get(4)).start();

		cookers.add(new Cooker(3));
		new Thread(cookers.get(5)).start();

	}

	public static boolean isAvailable(Apparatus apparatus) {
		if (apparatus == null) return true;
		if (apparatus.ordinal() == 0) return ovens.availablePermits() > 0;
		return stoves.availablePermits() > 0;
	}

	public static Semaphore getStoves() {
		return stoves;
	}

	public static Semaphore getOvens() {
		return ovens;
	}
}
