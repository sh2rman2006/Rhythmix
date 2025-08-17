# Используем образ с JDK 24
FROM amazoncorretto:21-alpine AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем wrapper Maven и конфигурационные файлы
COPY ./mvnw ./mvnw
COPY ./.mvn ./.mvn
COPY ./pom.xml ./pom.xml

# Загружаем зависимости для ускорения сборки
RUN chmod +x ./mvnw
RUN ./mvnw dependency:resolve -Dmaven.repo.local=/root/.m2/repository

# Копируем исходный код
COPY ./src ./src

# Собираем приложение
RUN ./mvnw package -DskipTests -Dmaven.repo.local=/root/.m2/repository

# Используем минимальный образ с JDK для запуска
FROM amazoncorretto:21-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]