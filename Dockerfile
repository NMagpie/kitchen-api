FROM adoptopenjdk/openjdk11:latest
EXPOSE 8080
ADD target/kitchen-api-0.0.1-SNAPSHOT.jar kitchen-api-0.0.1-SNAPSHOT.jar
ADD /configK.txt configK.txt
ENTRYPOINT ["java", "-jar", "/kitchen-api-0.0.1-SNAPSHOT.jar"]