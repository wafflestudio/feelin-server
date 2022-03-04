FROM lpicanco/java11-alpine
VOLUME /tmp
ARG JAR_FILE=~/build/*.jar
# Add into docker container
COPY ${JAR_FILE} ~/deploy/app.jar
# execute app.jar when running docker
ENTRYPOINT ["~/build/run.sh"]
