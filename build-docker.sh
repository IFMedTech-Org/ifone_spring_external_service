#!/bin/bash

VERSION=$1
if [ -z "$VERSION" ]; then
  echo "Usage: ./build-docker.sh <version>"
  exit 1
fi

JAR_NAME=ifone_spring_external_service-$VERSION.jar
ECR_REPO="872515288690.dkr.ecr.ap-south-1.amazonaws.com/ifmedtech/platform/ifone_spring_external_service"

if [ ! -f target/$JAR_NAME ]; then
  echo "❌ JAR not found: target/$JAR_NAME"
  echo "🔄 Please run: ./mvnw clean package  OR  ./gradlew build"
  exit 1
fi

# Enable BuildKit and use Buildx for cross-platform build
echo "🔧 Building image for platform linux/amd64..."
docker buildx build \
  --platform linux/amd64 \
  --build-arg JAR_NAME=$JAR_NAME \
  -t ifone-spring-external-service:$VERSION \
  -t $ECR_REPO:$VERSION \
  -t $ECR_REPO:latest \
  --load .

echo "✅ Docker image built and tagged:"
echo " - Local: ifone-spring-external-service:$VERSION"
echo " - ECR: $ECR_REPO:$VERSION"
echo " - ECR: $ECR_REPO:latest"
