FROM openjdk:8-jre-alpine
ENV MAVEN_OPTS=""
ARG BMRG_TAG

#COPY pom.xml .
#RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -Dmaven.test.skip=true -Dversion.name=$BMRG_TAG

VOLUME /tmp
EXPOSE 7732
RUN mv target/webhook-artifactory-$BMRG_TAG.jar service.jar
RUN sh -c 'touch /service.jar'
ENV JAVA_OPTS="-Dspring.profiles.active=local -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar service.jar" ]
