FROM openjdk:11-jdk
ARG ENV
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar
EXPOSE 8080
CMD java -jar build/libs/feelin-social-api.jar --spring.profiles.active=$ENV --spring.config.additional-location=application-$ENV.yml
