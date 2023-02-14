# Use an openjdk image as the base image
FROM alpine:3.17.2
ARG PAT
ARG BRANCH_NAME
# Set the working directory in the container to /app
WORKDIR /app
RUN apk update
RUN apk add --no-cache git
RUN apk add --no-cache openjdk17
# Clone the private Github repository using a personal access token (PAT)
RUN git clone https://$PAT@github.com/Hempflingclub/cbzCompress.git --branch $BRANCH_NAME

# Compile the Java project
WORKDIR /app/cbzCompress
RUN ./gradlew shadowJar
RUN mv /app/cbzCompress/build/libs/cbzCompress-*-all.jar /app/cbzCompress.jar
RUN ./gradlew clean # Remove persistent libs (maybe, too be ensured)
WORKDIR /app

RUN rm -rf /tmp/* /var/tmp/*
RUN rm -rf cbzCompress
RUN rm -rf ~/.gradle
RUN rm -rf /root
# Run the .jar file when the container starts
CMD ["java", "-jar", "/app/cbzCompress.jar","/app/in","/app/tmp","/app/tmpOut","/app/out","15"]
