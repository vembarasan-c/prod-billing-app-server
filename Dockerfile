# Use official Eclipse Temurin JDK 21 (Java 21)
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml and download dependencies first (cache layer)
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy full source
COPY src src

# Build your app
RUN ./mvnw package -DskipTests

# Run the app
CMD ["java", "-jar", "target/*.jar"]
