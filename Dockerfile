FROM openjdk:11-jdk
ARG ENV
ENV APP_ENV $ENV
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar
EXPOSE 8080
CMD java -jar build/libs/feelin-social-api.jar --spring.profiles.active=$APP_ENV --spring.config.additional-location=application-$APP_ENV.yml
