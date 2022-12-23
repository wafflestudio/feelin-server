FROM openjdk:11-jdk
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar
EXPOSE 8080
CMD java -jar build/libs/feelin-social-api.jar --spring.profiles.active=dev --spring.config.additional-location=application-dev.yml
