FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -yq make unzip

WORKDIR /

COPY / .

RUN ./gradlew installDist

CMD ./build/install/app/bin/app