FROM openjdk:11-jdk
VOLUME /tmp
ARG JAR_FILE=./build/libs/*.jar
# Add into docker container
COPY ${JAR_FILE} app.jar
# Execute app.jar when running docker
ENTRYPOINT ["./wait‑for‑it.sh", "database:3306", "‑‑", "java", "-jar", "app.jar",  "--spring.profiles.active=dev", "--spring.config.additional-location=resources/application-mail.yml"]
