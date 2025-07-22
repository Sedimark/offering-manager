# Use an OpenJDK base image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle build files and source code
COPY build/libs/offeringManager-0.02.jar .

# Expose the port your application runs on (optional)
EXPOSE 8080

# Specify the entry point (adjust if your jar file name or main class differs)
CMD ["java", "-jar", "offeringManager-0.02.jar"]
