#!/bin/bash

if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
then
    # Remove any anonymous volumes attached to containers (-v)
    sudo docker-compose -f ~/deploy/docker-compose.yml down -v
    # build images and run containers
    echo "[Deploy] : Stopping previous Application (dev)"
fi
