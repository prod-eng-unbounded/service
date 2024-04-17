FROM gradle:7.2.0-jdk11 as build

COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build clean --no-daemon


FROM openjdk:11
COPY --from=build /home/gradle/src/build/libs/hello-0.0.1-SNAPSHOT.jar /hello/libs/hello.jar

WORKDIR /hello/libs/

CMD ["java", "-jar","/hello/libs/hello.jar"]