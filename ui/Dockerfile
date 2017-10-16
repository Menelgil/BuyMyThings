FROM openjdk:8

ADD conf conf
ADD start-service start-service

VOLUME /var/log/play

CMD rm -f RUNNING_PID; \
    chmod +x ./start-service; \
    ./start-service

EXPOSE 9000