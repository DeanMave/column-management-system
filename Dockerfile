FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]