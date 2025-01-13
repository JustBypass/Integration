FROM openjdk:22
EXPOSE 9091
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} testingsystem.jar
ENTRYPOINT ["java","-jar","/testingsystem.jar"]