package com.kitchenapi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitchenapi.apparatus.Apparatus;
import com.kitchenapi.cooker.Cooker;
import com.kitchenapi.food.Foods;
import com.kitchenapi.order.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class KitchenApiApplication {

    public static final ArrayList<Order> orders = new ArrayList<>();

    private static final ArrayList<Cooker> cookers = new ArrayList<>();
    private static final SpringApplication app = new SpringApplication(KitchenApiApplication.class);
    private static TimeUnit timeUnit;
    private static TimeUnit restTime;
    private static String URL;
    private static Semaphore stoves;
    private static Semaphore ovens;
    private static int cookersSize;

    public static void main(String[] args) throws InterruptedException {

        initialization();

        if (stoves == null) stoves = new Semaphore((int) Math.round(cookersSize / 1.75), true);
        if (ovens == null) ovens = new Semaphore((int) Math.round(cookersSize / 3.5), true);

        if (cookers.isEmpty())
            createCookers();

        for (Cooker cooker : cookers) {
            System.out.println(cooker);
        }

        System.out.println("Ovens: " + ovens.availablePermits() + ", Stoves: " + stoves.availablePermits());

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

    public static Boolean isApparatusFree(Apparatus apparatus) {
        if (apparatus == Apparatus.oven) return ovens.availablePermits() > 0;
        if (apparatus == Apparatus.stove) return stoves.availablePermits() > 0;
        return true;
    }

    public static void acquireApparatus(Apparatus apparatus) {
        try {
            if (apparatus == Apparatus.oven) ovens.acquire();
            if (apparatus == Apparatus.stove) stoves.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void releaseApparatus(Apparatus apparatus) {
        if (apparatus == Apparatus.oven) ovens.release();
        if (apparatus == Apparatus.stove) stoves.release();
    }

    private static void createCookers() {
        //at least one Cooker Chief so if there will be only one cooker, thus kitchen can work properly.
        Cooker cooker = new Cooker(3);
        cooker.start();
        cookers.add(cooker);
        cookersSize--;
        while (cookersSize > 0) {
            cooker = new Cooker();
            cooker.start();
            cookers.add(cooker);
            cookersSize--;
        }
    }

    private static void addCookers(int rank, int number) {
        for (int i = 0; i < number; i++) {
            Cooker cooker = new Cooker(rank);
            cooker.start();
            cookers.add(cooker);
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

        try {

            String str;

            Scanner scanner = new Scanner(config);

            str = scanner.nextLine();

            timeUnit = TimeUnit.valueOf(str);

            String port = scanner.nextLine();

            if (!port.matches("^\\d{4,5}$")) parsingError(2);

            app.setDefaultProperties(Collections.singletonMap("server.port", port));

            app.run();

            restTime = TimeUnit.values()[timeUnit.ordinal() - 1];

            URL = scanner.nextLine();

            if (!URL.matches("((https?://[\\w-]+)|(((https?://)?\\d{1,3}\\.){3}(\\d{1,3})(/\\d+)?)):\\d{4,5}"))
                parsingError(3);

            if (scanner.hasNextLine()) str = scanner.nextLine();

            if (isNumberLine(str)) {

                String[] stringInts = str.split(" ");

                for (int i = 0; i < 3; i++) {
                    int number = Integer.parseInt(stringInts[i]);
                    addCookers(i + 1, number);
                }

                cookersSize = cookers.size();

                if (scanner.hasNextLine()) str = scanner.nextLine();

            } else if (isNumber(str)) {

                cookersSize = Integer.parseInt(str);

                if (scanner.hasNextLine()) str = scanner.nextLine();

            }

            if (cookersSize == 0) parsingError(4);

            createApparatus(str);

            if (scanner.hasNextLine()) createApparatus(scanner.nextLine());

            scanner.close();

            parseMenu();

        } catch (NoSuchElementException e) {
            parsingError(0);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            parsingError(1);
        } catch (FileNotFoundException e) {
            parsingError(-1);
        }

        if (timeUnit.ordinal() < 3) {
            System.out.println("!WARNING! The app supports timeUnits less, than seconds," +
                    " but some POST requests can be sent for more, than 200 ms, so the" +
                    " rating of the restaurant can be lowered to 0*!");
            TimeUnit.SECONDS.sleep(1);
        }
    }

    private static boolean isNumberLine(String str) {
        if (str == null) return false;
        return str.matches("^\\d+ \\d+ \\d+$");
    }

    private static boolean isNumber(String str) {
        if (str == null) return false;
        return str.matches("^\\d+$");
    }

    private static void parseMenu() throws InterruptedException {
        try {

            ObjectMapper mapper = new ObjectMapper();

            mapper.readValue(Paths.get("menu.json").toFile(), Foods[].class);

        } catch (JsonMappingException | JsonParseException e) {
            parsingError(-3);
        } catch (IOException e) {
            parsingError(-2);
        }
    }

    private static void parsingError(int intCase) throws InterruptedException {
        if (intCase > -1) System.out.println("Wrong data in config-file! Config file has to contain by lines:" +
                "\n1. Time units by capslock (e.g. MILLISECONDS, SECONDS, MICROSECONDS)" +
                "\n2. Free port to be reserved for this server" +
                "\n3. IPv4 address or URL of DinningHall and its port (e.g. http://localhost:8081)" +
                "\n4. Number of cookers OR Three integers represent number of cookers for every rank (e.g. first integer - rank one and so on)" +
                "\n5. (Optional) Number of ovens written as \"ovens %integer%\"" +
                "\n6. (Optional) Number of stoves written as \"stoves %integer%\"");
        switch (intCase) {
            case -3:
                System.out.println("ERROR: PARSING MENU");
                break;
            case -2:
                System.out.println("\"menu.json\" file have to be in the same directory as jar file or project");
                break;
            case -1:
                System.out.println("\"configK.txt\" file have to be in the same directory as jar file or project");
                break;
            case 0:
                System.out.println("ERROR: WRONG NUMBER OF LINES");
                break;
            case 1:
                System.out.println("ERROR IN LINE 1: TIMEUNITS");
                break;
            case 2:
                System.out.println("ERROR IN LINE 2: Port of this server");
                break;
            case 3:
                System.out.println("ERROR IN LINE 3: ADDRESS OR IP");
                break;
            case 4:
                System.out.println("ERROR IN LINE 4: Numbers of cookers");
                break;
        }

        TimeUnit.SECONDS.sleep(10);

        System.exit(1);
    }

}