#!/bin/bash

if [ "$DEPLOYMENT_GROUP_NAME" == "dev" ]
then
    docker-compose -f ~/deploy/docker-compose.yml down -v
    echo "[Deploy] : Stopping previous Application (dev)"
fi
