package com.example.kitchenapi;

import com.example.kitchenapi.apparatus.Apparatus;
import com.example.kitchenapi.cooker.Cooker;
import com.example.kitchenapi.order.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class KitchenApiApplication {

	//timeUnit only greater, than NANOSECONDS
	private static TimeUnit timeUnit;

	private static TimeUnit restTime;

	private static String URL;

	public static final ArrayList<Order> orders = new ArrayList<>();

	private static Semaphore stoves;

	private static Semaphore ovens;

	private static final ArrayList<Cooker> cookers = new ArrayList<>();

	private static int cookersSize;

	public static void main(String[] args) throws InterruptedException {

		SpringApplication.run(KitchenApiApplication.class, args);

		initialization();

		stoves = new Semaphore((int) Math.round(cookersSize / 1.75),true);

		ovens = new Semaphore((int) Math.round(cookersSize / 3.5),true);

		addCookers();

	}

	private static void parsingError() {
		System.out.println("Wrong data in config-file! Config file has to contain by lines:" +
				"1. Time units by capslock (e.g. MILLISECONDS, SECONDS, MICROSECONDS)" +
				"2. IPv4 address or URL of DinningHall and its port (e.g. http://localhost:8081)" +
				"3. number of Cookers in Kitchen (integer)");
		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
		}
		System.exit(1);
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

	private static void addCookers() {
		//at least one Cooker Chief so if there will be only one cooker, thus kitchen can work properly.
		cookers.add(new Cooker(3));
		new Thread(cookers.get(0)).start();
		cookersSize--;
		while (cookersSize > 0) {
			cookers.add(new Cooker());
			new Thread(cookers.get(cookers.size()-1)).start();
			cookersSize--;
		}
	}

	public static TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public static TimeUnit getRestTime() {
		return restTime;
	}

	public static String getURL() {
		return URL;
	}

	private static void initialization() throws InterruptedException {
		File config = new File("configK.txt");

		Scanner scanner = null;

		try {
			scanner = new Scanner(config);
		} catch (FileNotFoundException e) {
			System.out.println("\"configK.txt\" file have to be in the same directory as jar file or project");
			TimeUnit.SECONDS.sleep(10);
			System.exit(1);
		}

		String tUnit;

		if (scanner.hasNextLine()) {tUnit = scanner.nextLine(); try {
			timeUnit = TimeUnit.valueOf(tUnit);
		} catch (IllegalArgumentException e) { parsingError(); }
		} else { parsingError(); }

		if (scanner.hasNextLine()) URL = scanner.nextLine(); else { parsingError(); }
		if (!URL.matches("(https?\\:\\/\\/\\w+\\:\\d{4})|((\\d{1,3}\\.){3}(\\d{1,3})(\\/\\d+)?)")) parsingError();

		if (scanner.hasNextLine()) try { cookersSize= scanner.nextInt(); }
		catch (InputMismatchException e) { parsingError(); }
		else { parsingError(); }
		if (cookersSize < 1) parsingError();

		scanner.close();

		restTime = TimeUnit.values()[timeUnit.ordinal()-1];


		if (timeUnit.ordinal() == 0) {
			System.out.println("Wrong timeUnit input. Exiting program...");
			TimeUnit.SECONDS.sleep(10);
			System.exit(1);
		}

		if (timeUnit.ordinal() < 3) {
			System.out.println("!WARNING! The app supports timeUnits less, than seconds," +
					" but some POST requests can be sent for more, than 200 ms, so the" +
					" rating of the restaurant can be lowered to 0*!");
			TimeUnit.SECONDS.sleep(10);
		}
	}
}