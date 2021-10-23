package com.example.kitchenapi;

import com.example.kitchenapi.apparatus.Apparatus;
import com.example.kitchenapi.cooker.Cooker;
import com.example.kitchenapi.order.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class KitchenApiApplication {

    public static final ArrayList<Order> orders = new ArrayList<>();
    private static final ArrayList<Cooker> cookers = new ArrayList<>();
    //timeUnit only greater, than NANOSECONDS
    private static TimeUnit timeUnit;
    private static TimeUnit restTime;
    private static String URL;
    private static Semaphore stoves;
    private static Semaphore ovens;
    private static int cookersSize;

    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(KitchenApiApplication.class, args);

        initialization();

        if (stoves == null) stoves = new Semaphore((int) Math.round(cookersSize / 1.75), true);
        if (ovens == null) ovens = new Semaphore((int) Math.round(cookersSize / 3.5), true);

        for (Cooker cooker : cookers) {
            cooker.setOvens(ovens);
            cooker.setStoves(stoves);
        }

        if (cookers.isEmpty())
            addCookers();

        for (Cooker cooker : cookers) {
            System.out.println(cooker);
        }

        System.out.println("Ovens: " + ovens.availablePermits() + ", Stoves: " + stoves.availablePermits());

    }

    public static boolean isAvailable(Apparatus apparatus) {
        if (apparatus == null) return true;
        if (apparatus.ordinal() == 0) return ovens.availablePermits() > 0;
        return stoves.availablePermits() > 0;
    }

    private static void addCookers() {
        //at least one Cooker Chief so if there will be only one cooker, thus kitchen can work properly.
        cookers.add(new Cooker(3));
        new Thread(cookers.get(0)).start();
        cookersSize--;
        while (cookersSize > 0) {
            cookers.add(new Cooker());
            new Thread(cookers.get(cookers.size() - 1)).start();
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

        String str;

        try {
            scanner = new Scanner(config);

            str = scanner.nextLine();

            timeUnit = TimeUnit.valueOf(str);

            URL = scanner.nextLine();

            if (!URL.matches("((https?://[\\w-]+)|(((https?://)?\\d{1,3}\\.){3}(\\d{1,3})(/\\d+)?)):\\d{4}"))
                parsingError(2);

            str = scanner.nextLine();

            if (isNumber(str)) cookersSize = Integer.parseInt(str);
            else parsingError(3);

            if (cookersSize < 1) parsingError(3);

            restTime = TimeUnit.values()[timeUnit.ordinal() - 1];

            str = scanner.nextLine();

            while (isNumber(str)) {
                cookers.add(new Cooker(Integer.parseInt(str)));
                new Thread(cookers.get(cookers.size() - 1)).start();
                if (scanner.hasNextLine()) str = scanner.nextLine();
                else str = null;
            }

            if (str != null) {
                createApparatus(str);
                if (scanner.hasNextLine()) createApparatus(scanner.nextLine());
            }

            if (cookers.size() != 0 && cookers.size() != cookersSize) parsingError(4);

        } catch (NoSuchElementException e) {
            parsingError(0);
        } catch (IllegalArgumentException e) {
            parsingError(1);
        } catch (FileNotFoundException e) {
            parsingError(-1);
        }

        scanner.close();

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

    private static boolean isNumber(String str) {
        if (str == null) return false;
        return str.matches("^\\d+$");
    }

    private static void parsingError(int intCase) throws InterruptedException {
        System.out.println("Wrong data in config-file! Config file has to contain by lines:" +
                "\n1. Time units by capslock (e.g. MILLISECONDS, SECONDS, MICROSECONDS)" +
                "\n2. IPv4 address or URL of DinningHall and its port (e.g. http://localhost:8081)" +
                "\n3. Number of Cookers in Kitchen (integer)" +
                "\n4. (Optional) Rank of every cooker on new line" +
                "\n5. Number of ovens written as \"ovens %integer%\"" +
                "\n6. Number of stoves written as \"stoves %integer%\"");
        switch (intCase) {
            case -1:
                System.out.println("\"configK.txt\" file have to be in the same directory as jar file or project");
                TimeUnit.SECONDS.sleep(10);
                System.exit(1);
                break;
            case 0:
                System.out.println("ERROR: WRONG NUMBER OF LINES");
                break;
            case 1:
                System.out.println("ERROR IN LINE 1: TIMEUNITS");
                break;
            case 2:
                System.out.println("ERROR IN LINE 2: ADDRESS OR IP");
                break;
            case 3:
                System.out.println("ERROR IN LINE 3: NUMBER OF COOKERS");
                break;
            case 4:
                System.out.println("ERROR: NO CORRESPONDENCE BETWEEN NUMBER OF COOKERS AND THEIR RANKS");
                break;
        }

        TimeUnit.SECONDS.sleep(20);

        System.exit(1);
    }

    private static void createApparatus(String str) throws InterruptedException {
        if (str.matches("^ovens \\d+$") && ovens == null) {
            ovens = new Semaphore(Integer.parseInt(str.split(" ")[1]), true);
            return;
        }

        if (str.matches("^stoves \\d+$") && stoves == null) {
            stoves = new Semaphore(Integer.parseInt(str.split(" ")[1]), true);
            return;
        }

        parsingError(0);

    }

}