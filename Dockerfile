FROM debian:stable-20230208-slim
ARG PAT
ARG BRANCH_NAME
ENV WAIT_TIME=15
ENV QUALITY=70
WORKDIR /app
RUN apt-get update
RUN apt-get install git openjdk-17-jdk-headless openjdk-17-jre-headless -y
# Clone the private Github repository
RUN git clone https://github.com/Hempflingclub/cbzCompress.git --branch $BRANCH_NAME
WORKDIR /app/cbzCompress
RUN ./gradlew build
RUN mv build/libs/cbzCompress-*-all.jar /app/cbzCompress.jar
WORKDIR /app
RUN rm -rf cbzCompress
# Remove all Unneccessary Packages
RUN apt-get remove git openjdk-17-jdk-headless -y
RUN apt-get autoremove -y
# Run the .jar file when the container starts
CMD java -jar /app/cbzCompress.jar /app/in /app/tmp /app/tmpOut /app/out $WAIT_TIME $QUALITY
