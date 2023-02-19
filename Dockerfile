FROM debian:stable-20230208-slim
ARG PAT
ARG BRANCH_NAME
ENV WAIT_TIME=15
WORKDIR /app
RUN apt-get update
RUN apt-get install git python3 pip git p7zip openjdk-17-jre-headless -y
# Clone the private Github repository using a personal access token (PAT)
RUN git clone https://$PAT@github.com/Hempflingclub/cbzCompress.git --branch $BRANCH_NAME
RUN pip install cyberdrop-dl
# Download Compiled Java project
WORKDIR /app/cbzCompress
RUN cyberdrop-dl -i dl_link
RUN mv Downloads/*/*.7z ./
RUN 7zr x build.7z
RUN mv cbzCompress-*-all.jar /app/cbzCompress.jar
WORKDIR /app
RUN rm -rf cbzCompress
# Uninstall every pip module
RUN pip freeze | xargs pip uninstall -y || true
# Remove all Unneccessary Packages
RUN apt-get remove git python3 pip git p7zip -y
RUN apt-get autoremove -y
# Run the .jar file when the container starts
CMD java -jar /app/cbzCompress.jar /app/in /app/tmp /app/tmpOut /app/out $WAIT_TIME
