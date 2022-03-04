#!/bin/bash

if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
then
    # Remove any anonymous volumes attached to containers
    docker-compose -f docker-compose.yml rm -v
    # build images and run containers
    echo "[Deploy] : Running new Application"
    docker-compose -f docker-compose.yml up --detach --renew-anon-volumes
fi
