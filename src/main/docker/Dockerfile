FROM openjdk:17.0-slim

ENV APP_FOLDER /app
ENV JAVA_OPTS ""

VOLUME /app/config
WORKDIR /app

ADD application.yml ${APP_FOLDER}/application.yml
ADD startup.sh ${APP_FOLDER}
ADD music-service*.jar /app/app.jar

EXPOSE 8080

CMD ["./startup.sh"]


