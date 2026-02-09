# Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY build/libs/client-service-*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -g 1000 app && adduser -u 1000 -G app -D app
WORKDIR /app

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

USER app
EXPOSE 8081
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "org.springframework.boot.loader.launch.JarLauncher"]
