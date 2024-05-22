FROM openjdk:21-jdk-alpine3.13

EXPOSE 5454

ADD target/netology-cloud-storage-0.0.1-SNAPSHOT.jar diploma.jar

ENTRYPOINT ["java", "-jar", "FiliniCloudStorage.jar"]