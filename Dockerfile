FROM lpicanco/java11-alpine
VOLUME /tmp
ARG JAR_FILE=./build/libs/*.jar
# Add into docker container
COPY ${JAR_FILE} app.jar
# execute app.jar when running docker
ENTRYPOINT ["java", "-jar", "app.jar",  "--spring.profiles.active=prod"]
