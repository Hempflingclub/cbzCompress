# Use an openjdk image as the base image
FROM ubuntu:latest
ARG PAT
ARG BRANCH_NAME
# Set the working directory in the container to /app
WORKDIR /app
RUN apt-get update
RUN apt-get install git openjdk-17-jdk-headless -y
# Clone the private Github repository using a personal access token (PAT)
RUN git clone https://$PAT@github.com/Hempflingclub/cbzCompress.git --branch $BRANCH_NAME

# Compile the Java project
WORKDIR /app/cbzCompress
RUN ./gradlew shadowJar
RUN ./gradlew clean # Remove persistent libs (maybe, too be ensured)
RUN mv /app/cbzCompress/build/libs/cbzCompress-*-all.jar /app/cbzCompress.jar
WORKDIR /app

# Remove the build directory and other files to keep the image small
RUN rm -rf cbzCompress
#Uninstall JDK and Install JRE
RUN apt-get remove openjdk-17-jdk-headless -y
RUN apt-get install openjdk-17-jre-headless -y
RUN apt-get autoremove -y
# Run the .jar file when the container starts
CMD ["java", "-jar", "/app/cbzCompress.jar","/app/in","/app/tmp","/app/tmpOut","/app/out","15"]
