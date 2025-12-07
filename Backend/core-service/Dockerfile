FROM amazoncorretto:21-alpine AS build

WORKDIR /app

COPY ./mvnw ./mvnw
COPY ./.mvn ./.mvn
COPY ./pom.xml ./pom.xml

RUN chmod +x ./mvnw

# Качаем зависимости (можно go-offline, так стабильнее)
RUN ./mvnw dependency:go-offline -Dmaven.repo.local=/root/.m2/repository

# Копируем исходники
COPY ./src ./src

# Собираем приложение
RUN ./mvnw package -DskipTests -Dmaven.repo.local=/root/.m2/repository

# Runtime-образ
FROM amazoncorretto:21-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
