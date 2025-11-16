FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy Maven wrapper files
COPY mvnw .
COPY mvnw.cmd .

# Give execute permission to Maven wrapper
RUN chmod +x mvnw

# Copy only pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (skip tests)
RUN ./mvnw dependency:go-offline -B || true

# Copy the full project
COPY src ./src

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Run the application
CMD ["java", "-jar", "target/*.jar"]
