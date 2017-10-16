FROM openjdk:8

ADD start-service start-service

VOLUME /var/log/play

CMD rm -f RUNNING_PID; \
    ./start-service

EXPOSE 9000