#Imagen modelo
FROM eclipse-temurin:21.0.5_11-jdk
#informar del puerto que se va a exponer
EXPOSE 8080
#DEFINIR DIRECTORIO RAIZ DEL CONTENEDOR
WORKDIR /root
#COPIAR ARCHIVO dentro del contenedor
COPY ./pom.xml /root
#agregar el mvn wrapper al contenedor
COPY ./.mvn /root/.mvn
#agregar el archivo mvnw al contenedor
COPY ./mvnw /root
#descargar dependencias
RUN ./mvnw dependency:go-offline
#COPIAR EL CODIGO FUENTE AL CONTENEDOR
COPY ./src /root/src
#construyo la aplicacion
RUN ./mvnw clean install -DskipTests
#levantar la app cuando el contenedor se inicie
ENTRYPOINT ["java","-jar","/root/target/demo-0.0.1-SNAPSHOT.jar"]