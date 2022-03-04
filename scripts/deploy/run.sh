#!bin/bash

nohup java -jar -Dspring.profiles.active=prod ~/deploy/app.jar > ~/deploy/nohup.out 2>&1 &
