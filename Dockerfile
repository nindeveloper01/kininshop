# Use the Eclipse temurin alpine official image
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy project files
COPY . ./

# Ensure the Maven wrapper is executable
RUN chmod +x ./mvnw

# Build the app
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install

# Run the app
CMD ["sh", "-c", "java -jar target/*.jar"]