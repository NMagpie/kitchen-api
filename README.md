# Lab #1 Kitchen Api (Main part)

### Author: _Sorochin Nichita, FAF-191_

---

## Technologies used:
* ### JDK: 11
* ### Spring Boot: 2.5.5

## Configure

### To run project with and configure ins values, there is a config file named `configK.txt`, where:
### 1. TimeUnits to run project with (written with capsLock, because represented as Enum TimeUnit in Java) e.g. MILLISECONDS, SECONDS, etc.
### 2. Free port to be reserved for this server.
### 2. IP-address or URL of other side (Dinning Hall) with its port. E.g. http://localhost:8080
### 3. Number of work units (In kitchen, number of cookers) as integer number (ranks will be generated randomly) 
### OR
### 3. Three integers, representing quantity of cookers for every rank (e.g. 3 2 1 means three cookers of rank 1, two cokers of rank 2 and one cooker of rank 3)
### 4 and 5. Number of cooking apparatuses on separate lines (e.g. "stoves 2", and "ovens 1")

## Menu

### User can create their custom menu, which is formed as json-file placed in repository named `menu.json`

---

## !IMPORTANT! To make both projects run properly, run `kitchen-api` first, and only then `dinning_hall-api` To launch with `food ordering service` and `client service` run projects in proposed order:

### 1. Food Ordering service
### 2. Kitchen
### 3. Dinning-Hall
### 4. Client Service

### If you want to run both projects in docker, in `dinning_hall-api` repository is located Shell script.
### 1. Clone both projects' directories into one general directory. 
### (e.g. "%commonD%\dinning_hall-api\" and "%commonD%\kitchen-api\")
### 2. Put both `1step.sh` and `2step.sh` scripts into general directory ("%commonD%")
### 3. Now you can create Docker Images, containters and run them in the same network just executing scripts.
