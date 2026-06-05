# ============================================================
#  Etape 1 : construction du .jar avec Maven
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# On copie d'abord le pom.xml pour profiter du cache des dependances
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Puis le code source, et on construit (sans lancer les tests)
COPY src ./src
RUN mvn -B clean package -DskipTests

# ============================================================
#  Etape 2 : image legere qui execute le .jar
# ============================================================
FROM eclipse-temurin:17-jre
WORKDIR /app

# On recupere le .jar construit a l'etape precedente
COPY --from=build /app/target/*.jar app.jar

# Render fournit le port via la variable PORT
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]