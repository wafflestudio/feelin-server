#!/bin/bash

if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
then
    # Remove any anonymous volumes attached to containers (-v)
    sudo docker-compose -f ~/deploy/docker-compose.yml rm -v
    # Recreate anonymous volumes instead of retrieving data from the previous containers (--renew-anon-volumes)
    sudo docker-compose -f ~/deploy/docker-compose.yml up -d --renew-anon-volumes
    echo "[Deploy] : Running new Application (dev)"
fi
if [ "$DEPLOYMENT_GROUP_NAME" == "prod" ]
then
    sudo docker-compose -f ~/deploy/docker-compose.yml up --no-deps -d application
    echo "[Deploy] : Running new Application (prod)"
fi
