# ─── Stage 1 : Build ────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Téléchargement des dépendances en cache séparé de la compilation
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Compilation
COPY src ./src
RUN mvn package -DskipTests -B -q

# ─── Stage 2 : Runtime ──────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Ajout d'un utilisateur non-root (syntaxe Debian/Ubuntu)
RUN groupadd -r prepa && useradd -r -g prepa prepa
USER prepa

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
