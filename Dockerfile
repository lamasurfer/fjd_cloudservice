FROM bellsoft/liberica-openjdk-alpine
EXPOSE 8081
ADD build/libs/*.jar cloudservice.jar
ENTRYPOINT ["java","-jar","/cloudservice.jar"]