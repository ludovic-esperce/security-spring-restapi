# utilisation d'une image recommandée pour sa légèreté 
FROM bellsoft/liberica-openjdk-alpine:17
EXPOSE 8000/tcp
COPY target/hostel-0.0.1-SNAPSHOT.jar hostel-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/hostel-0.0.1-SNAPSHOT.jar"]

# FROM maven:3.8.6-eclipse-temurin-17-alpine@sha256:e88c1a981319789d0c00cd508af67a9c46524f177ecc66ca37c107d4c371d23b AS builder
# WORKDIR /build
# COPY . .
# RUN mvn clean package -DskipTests
 
# FROM eclipse-temurin:17.0.5_8-jre-alpine@sha256:02c04793fa49ad5cd193c961403223755f9209a67894622e05438598b32f210e
# WORKDIR /opt/app
# RUN addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser
# COPY --from=builder /build/target/mydockerbestpracticesplanet-0.0.1-SNAPSHOT.jar app.jar
# RUN chown -R javauser:javauser .
# USER javauser
# HEALTHCHECK --interval=30s --timeout=3s --retries=1 CMD wget -qO- http://localhost:8080/actuator/health/ | grep UP || exit 1
# ENTRYPOINT ["java", "-jar", "app.jar"]