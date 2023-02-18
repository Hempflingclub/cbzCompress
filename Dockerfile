# Use an openjdk image as the base image
FROM alpine:3.17.2
ARG PAT
ARG BRANCH_NAME
ARG WAIT_TIME
# Set the working directory in the container to /app
WORKDIR /app
RUN apk update
RUN apk add --no-cache git
RUN apk add --no-cache openjdk17
RUN apk add --no-cache 7zip
RUN apk add --no-cache curl
RUN apk add --no-cache jq
# Clone the private Github repository using a personal access token (PAT)
RUN git clone https://$PAT@github.com/Hempflingclub/cbzCompress.git --branch $BRANCH_NAME

# Download Compiled Java project
WORKDIR /app/cbzCompress/.github/scripts
RUN ./downloadLatest.sh
RUN 7zip x build.7z
RUN mv cbzCompress-*-all.jar /app/cbzCompress.jar
WORKDIR /app
RUN rm -rf cbzCompress
# Run the .jar file when the container starts
CMD ["java", "-jar", "/app/cbzCompress.jar","/app/in","/app/tmp","/app/tmpOut","/app/out","$WAIT_TIME"]
