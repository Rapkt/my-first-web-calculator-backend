# Step 1: Use OpenJDK with Maven installed
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Step 2: Copy project files
WORKDIR /app
COPY . .

# Step 3: Build the JAR
RUN mvn clean package -DskipTests

# Step 4: Run the JAR in a lightweight JDK image
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
