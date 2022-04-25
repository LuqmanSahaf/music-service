#!/bin/bash

cd ${APP_FOLDER}

java $JAVA_OPTS -jar app.jar -Dspring.config.location=./application.yml