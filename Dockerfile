FROM openjdk:11
EXPOSE 8080
COPY target/file-storage.jar file-storage.jar
ENTRYPOINT java -jar file-storage.jar