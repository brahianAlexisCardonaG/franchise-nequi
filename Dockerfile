# IMAGEN MODELO
FROM eclipse-temurin:17-jdk

# DEFINIR DIRECTORIO RAIZ DE NUESTRO CONTENEDOR
WORKDIR /root

# COPIAR Y PEGAR ARCHIVOS DENTRO DEL CONTENEDOR
COPY build.gradle /root
COPY settings.gradle /root
COPY gradlew /root
COPY gradle /root/gradle

# DESCARGAR LAS DEPENDENCIAS
RUN chmod +x ./gradlew \
    && ./gradlew dependencies --no-daemon

# COPIAR EL CODIGO FUENTE DENTRO DEL CONTENEDOR
COPY . /root

RUN chmod +x gradlew

# CONSTRUIR NUESTRA APLICACION
RUN ./gradlew clean build --no-daemon

# INFORMAR EL PUERTO DONDE SE EJECUTA EL CONTENEDOR (INFORMATIVO)
EXPOSE 8085

# LEVANTAR NUESTRA APLICACION CUANDO EL CONTENEDOR INICIE
ENTRYPOINT ["java","-jar","/root/build/libs/franchise-0.0.1-SNAPSHOT.jar"]