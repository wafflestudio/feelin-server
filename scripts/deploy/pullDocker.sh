#!bin/bash

PORT_USING_PID=$(lsof -ti tcp:8080)

if [[ -z $PORT_USING_PID ]];
then
    echo "[Deploy] : Port available"
    # docker login
    docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD
    # pull docker image
    if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
    then
    docker-compose -f ~/build/docker-compose.yml pull
    fi

else
    echo "[Deploy] : Another application is using port"
    echo "[Deploy] : Stopping application using port"
    kill -15 $PORT_USING_PID
    sleep 5
fi