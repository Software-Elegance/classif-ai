#Use alpine

# FROM openjdk:14-jdk
FROM openjdk:11-jre-slim


ARG DOCKER_SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE $DOCKER_SPRING_PROFILES_ACTIVE

ENV TZ Africa/Nairobi


# Add Labels
LABEL project="ClassifAI"
LABEL maintainer="itambo.ibrahim@gmail.com"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8761 available to the world outside this container
EXPOSE 8080

# The fat jar
ARG JAR_FILE=build/libs/*.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]


#Aftermath
#========================================
#Fat jar
#./gradlew frontend:bootJar

#Dockerize
#cd frontend
#docker build -t zeguru/classifai-frontend:0.4 .

#Push
#docker login
#docker push zeguru/classifai-frontend:0.4

