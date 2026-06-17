# ─────────────────────────────────────────────────────────────
# Étape 1 : build du JAR avec Maven (Java 17)
# ─────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache des dépendances : on copie d'abord le pom seul
COPY pom.xml .
RUN mvn -q dependency:go-offline -B

# Puis le code source et on construit
COPY src ./src
RUN mvn -q clean package -DskipTests -B

# ─────────────────────────────────────────────────────────────
# Étape 2 : image d'exécution légère (JRE seul)
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app

# Utilisateur non-root pour la sécurité
RUN useradd -r -u 1001 navi
COPY --from=build --chown=navi:navi /app/target/navi-education-1.0.0.jar app.jar
USER navi

# La plateforme d'hébergement fournit le port via $PORT (Render, Railway...)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

# server.port lit $PORT ; profil prod (PostgreSQL) activé par défaut
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
