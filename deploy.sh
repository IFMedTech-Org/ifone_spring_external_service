#!/bin/bash
set -e

# === Configuration ===
SERVICE_NAME="ifone_spring_external_service"
PORT="8086"
PROFILE="dev" # change to "prod" for production
REGION="ap-south-1"
REPO_BASE="872515288690.dkr.ecr.${REGION}.amazonaws.com/ifmedtech/platform"
IMAGE="${REPO_BASE}/${SERVICE_NAME}:latest"
DEPLOY_DIR="/home/ubuntu/projects/ifone/spring_external_service"

# === Use current project directory ===
DEPLOY_DIR="/home/ubuntu/projects/ifone/spring_external_service"
LOG_DIR="${DEPLOY_DIR}/logs"
CONFIG_DIR="${DEPLOY_DIR}"
LOG_FILE="${LOG_DIR}/${SERVICE_NAME}.log"
DOWNLOAD_DIR="${DEPLOY_DIR}/download"

# === Ensure logs folder exists ===
mkdir -p "$LOG_DIR"

echo "=== Logging in to ECR ==="
aws ecr get-login-password --region $REGION | \
  docker login --username AWS --password-stdin ${REPO_BASE}

echo "=== Pulling latest image: $IMAGE ==="
docker pull $IMAGE

echo "=== Stopping and removing existing container (if any) ==="
docker stop $SERVICE_NAME || true
docker rm $SERVICE_NAME || true

echo "=== Starting container on port $PORT with Spring profile: $PROFILE ==="
docker run -d \
  -p ${PORT}:${PORT} \
  --name $SERVICE_NAME \
  -v ${CONFIG_DIR}:/config \
  -v ${DOWNLOAD_DIR}:/app/download \
  -e "SPRING_PROFILES_ACTIVE=${PROFILE}" \
  $IMAGE \
  --spring.config.additional-location=classpath:/,file:/config/

echo "=== Cleaning up old images in ECR ==="

aws ecr list-images \
  --repository-name ifmedtech/platform/ifone_spring_external_service \
  --region ap-south-1 \
  --query 'imageIds[?imageTag!=`latest`]' \
  --output json > old_images.json

aws ecr batch-delete-image \
  --repository-name ifmedtech/platform/ifone_spring_external_service \
  --region ap-south-1 \
  --cli-input-json file://old_images.json || echo "No old images to delete"

echo "=== Waiting briefly for logs... ==="
sleep 2

echo "=== Showing initial logs (1 second stream) ==="
timeout 1s docker logs -f $SERVICE_NAME | tee $LOG_FILE || true
