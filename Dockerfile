FROM openjdk:11-jdk
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar
COPY build/libs/*.jar app.jar
# Execute app.jar when running docker
EXPOSE 8080
#ENTRYPOINT ["scripts/wait-for-it.sh", "database:3306", "--", "java", "-jar", "app.jar",  "--spring.profiles.active=dev", "--spring.config.additional-location=resources/application-dev.yml"]
CMD java -jar app.jar --spring.profiles.active=dev --spring.config.additional-location=application-dev.yml
