FROM maven:3-openjdk-14-slim AS build-container

COPY ./pom.xml ./pom.xml

RUN ["mvn", "dependency:resolve"]

COPY ./src ./src

RUN ["mvn", "verify"]

FROM openjdk:14

COPY --from=build-container ./target/portfolio-site-*.jar ./app/

COPY ./docker/rename-app-files.sh rename-app-files.sh
RUN "./rename-app-files.sh"

CMD ["java", "-jar", "./app.jar"]
