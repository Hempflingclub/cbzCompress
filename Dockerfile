FROM alpine:3.17.2
ARG PAT
ARG BRANCH_NAME
ENV WAIT_TIME=15
WORKDIR /app
RUN apk update
RUN apk add --no-cache git openjdk17-jre-headless 7zip python3-dev py3-pip gcc libc-dev linux-headers
# Clone the private Github repository using a personal access token (PAT)
RUN git clone https://$PAT@github.com/Hempflingclub/cbzCompress.git --branch $BRANCH_NAME
RUN pip install cyberdrop-dl
# Download Compiled Java project
WORKDIR /app/cbzCompress
RUN cyberdrop-dl -i dl_link
RUN mv Downloads/*/*.7z ./
RUN 7z x build.7z
RUN mv cbzCompress-*-all.jar /app/cbzCompress.jar
WORKDIR /app
RUN rm -rf cbzCompress
# Uninstall every pip module
RUN pip freeze | xargs pip uninstall -y || true
# Remove all Unneccessary Packages
RUN apk del git 7zip python3-dev py3-pip gcc libc-dev linux-headers
# Run the .jar file when the container starts
CMD ["java", "-jar", "/app/cbzCompress.jar","/app/in","/app/tmp","/app/tmpOut","/app/out","$WAIT_TIME"]
