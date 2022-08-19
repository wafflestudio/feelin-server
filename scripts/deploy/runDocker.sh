#!/bin/bash

if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
then
    # Remove any anonymous volumes attached to containers (-v)
    # sudo docker-compose -f ~/deploy/docker-compose.yml rm
    # Recreate anonymous volumes instead of retrieving data from the previous containers (--renew-anon-volumes)
    sudo docker-compose -f ~/deploy/docker-compose.yml up -d
    # build images and run containers
    echo "[Deploy] : Running new Application"
fi
