FROM maven:3-openjdk-14-slim AS build-container

COPY ./pom.xml ./pom.xml

RUN ["mvn", "dependency:go-offline"]

COPY ./src ./src

RUN ["mvn", "-Pprod", "-Dmaven.test.skip=true", "verify"]

FROM openjdk:14

RUN useradd --create-home web-app-user
USER web-app-user
WORKDIR /home/web-app-user

COPY --chown=web-app-user --from=build-container ./target/portfolio-site-*.jar ./app/
WORKDIR /home/web-app-user/app

COPY ./docker/rename-app-files.sh rename-app-files.sh
RUN ["/bin/bash", "./rename-app-files.sh"]

COPY ./frontend ./frontend

CMD ["java", "-jar", "./app.jar"]
