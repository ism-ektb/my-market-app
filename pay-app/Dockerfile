# ─────────────────────────────────────────────────────────────
# ЭТАП 1: Сборка
# ─────────────────────────────────────────────────────────────
FROM gradle:8.14-jdk21 AS builder
WORKDIR /app
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon
COPY src src
RUN gradle bootJar --no-daemon

# ─────────────────────────────────────────────────────────────
# ЭТАП 2: Запуск
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Создаём не-root пользователя для безопасности
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Копируем JAR с использованием wildcard (не зависит от версии)
COPY --from=builder /app/build/libs/*.jar app.jar

# Указываем порт приложения
EXPOSE 8081

# Запускаем от имени не-root пользователя
USER appuser

# JVM-оптимизации для контейнеров
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-jar", "app.jar"]
