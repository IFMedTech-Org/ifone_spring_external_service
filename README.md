# ifone_spring_external_service


docker run -p 8080:8080 \
-v $(pwd)/configs:/config \
my-spring-app \
--spring.config.location=file:/config/application-prod.yml

VERSION=0.0.2

docker build \
--build-arg JAR_FILE=target/ifone_spring_external_service-$VERSION.jar \
-t ifone-service:$VERSION \
-t ifone-service:stable \
.


VERSION=0.0.2

docker build \
--build-arg JAR_FILE=target/ifone_spring_external_service-$VERSION.jar \
-t ifone-service:$VERSION \
-t ifone-service:stable \
.

docker run -p 8086:8086 \
-v $(pwd)/configs:/config \
ifone-spring-external-service:0.0.1 \
--server.port=8086 \
--spring.config.location=file:/config/application-prod.yml