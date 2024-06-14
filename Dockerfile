FROM maven:3.9-amazoncorretto-21
VOLUME /tmp
WORKDIR /tmp/
COPY ./pom.xml /tmp/
RUN mvn dependency:resolve

COPY src /tmp/src
RUN mvn compile
RUN mvn package

VOLUME /playtakdb
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
# COPY target/takserver-jar-with-dependencies.jar takserver.jar  # this could be used when the jar file is built locally
COPY ./properties.xml /tmp/target/
EXPOSE 9998
EXPOSE 9999
EXPOSE 10000
WORKDIR /tmp/target
ENTRYPOINT exec java $JAVA_OPTS -jar takserver-jar-with-dependencies.jar
