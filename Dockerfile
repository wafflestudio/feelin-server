FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=./build/libs/*.jar
# Add into docker container
COPY ${JAR_FILE} app.jar
# Execute app.jar when running docker
ENTRYPOINT ["java", "-jar", "app.jar",  "--spring.profiles.active=prod", "-Dspring.config.additional-location=/resources/application-mail.yml"]
