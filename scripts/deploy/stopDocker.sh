#!/bin/bash

docker-compose down
docker stop $(docker ps -a -q)
