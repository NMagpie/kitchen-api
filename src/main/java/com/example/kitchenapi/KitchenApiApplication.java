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
	public static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;

	public static final TimeUnit restTime = TimeUnit.values()[timeUnit.ordinal()-1];

	public static final ArrayList<Order> orders = new ArrayList<>();

	private static final ArrayList<Apparatus> ovens = new ArrayList<>();

	private static final ArrayList<Apparatus> stoves = new ArrayList<>();

	public static void main(String[] args) {

		if (timeUnit.ordinal()<1) {
			System.out.println("Wrong timeUnit input. Exiting program...");
			System.exit(0);
		}

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

		for (int i = 0; i < Math.round(cookers.size() / 3.5); i++)
		addOven();
		for (int i = 0; i < Math.round(cookers.size() / 1.75); i++)
		addStove();

		RequestController.setCondition(condition);

		SpringApplication.run(KitchenApiApplication.class, args);

	}

	private static void addOven() {
		ovens.add(Apparatus.Oven);
	}

	private static void addStove() {
		stoves.add(Apparatus.Stove);
	}

	public static boolean isAvailable(Apparatus apparatus){
		if (apparatus == null) return true;
		if (apparatus.ordinal() == 0) {
			for (int i = 0; i < ovens.size(); i++)
				if (!ovens.get(i).isLocked()) return true;
		} else {
			for (int i = 0; i < stoves.size(); i++)
				if (!stoves.get(i).isLocked()) return true;
		}
		return false;
	}

	public static Apparatus getAvailableApp(Apparatus apparatus) {
		if (apparatus.ordinal() == 0) {
			for (int i = 0; i < ovens.size(); i++)
				if (!ovens.get(i).isLocked()) { ovens.get(i).lock(); return ovens.get(i); }
			int index = (int) (Math.random()*ovens.size());
			ovens.get(index).lock(); return ovens.get(index);
		} else {
			for (int i = 0; i < stoves.size(); i++)
				if (!stoves.get(i).isLocked()) { stoves.get(i).lock(); return stoves.get(i); }
			int index = (int) (Math.random()*stoves.size());
			stoves.get(index).lock(); return stoves.get(index);
		}

	}

}
