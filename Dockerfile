FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/offeringManager-0.09.jar ./offeringManager.jar

# Expose the port your application runs on (optional)
EXPOSE 8080

# Specify the entry point (adjust if your jar file name or main class differs)
CMD ["java", "-jar", "offeringManager.jar"]
