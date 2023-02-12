# Use an openjdk image as the base image
FROM openjdk:17-alpine
ARG PAT
ARG BRANCH_NAME
# Set the working directory in the container to /app
WORKDIR /app

# Clone the private Github repository using a personal access token (PAT)
RUN git clone https://$PAT@github.com/Hempflingclub/cbzCompress.git --branch $BRANCH_NAME .

# Compile the Java project
RUN ./cbzCompress/gradlew build

# Copy the .jar file from the build directory to the working directory
RUN cp ./cbzCompress/build/libs/cbzCompress-1.1.1-all.jar ./cbzCompress.jar #cbzCompress-1.1.1-arm64-all.jar for branch specific for instance

# Remove the build directory and other files to keep the image small
RUN rm -rf cbzCompress

# Run the .jar file when the container starts
CMD ["java", "-jar", "cbzCompress.jar","in","tmp","tmpOut","out","15"]