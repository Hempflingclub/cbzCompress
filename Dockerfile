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
WORKDIR ./cbzCompress
RUN ./gradlew build
WORKDIR ../
# Copy the .jar file from the build directory to the working directory
RUN cp ./cbzCompress/build/libs/cbzCompress-1.1.1-arm64-all.jar ./cbzCompress.jar #cbzCompress-1.1.1-arm64-all.jar for branch specific for instance

# Remove the build directory and other files to keep the image small
RUN rm -rf cbzCompress

# Run the .jar file when the container starts
CMD ["java", "-jar", "cbzCompress.jar","in","tmp","tmpOut","out","15"]
