# Используем официальный образ OpenJDK 21
FROM tomcat:10-jdk21


# Копируем приложение
COPY target/REST-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/rest.war

WORKDIR /usr/local/tomcat

# Открываем порт
EXPOSE 8080
