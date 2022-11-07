FROM openjdk:11
VOLUME /tmp
EXPOSE 8093
ADD ./target/ms-loan-0.0.1-SNAPSHOT.jar ms-loan.jar
ENTRYPOINT ["java","-jar","/ms-loan.jar"]