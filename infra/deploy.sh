#!/bin/bash
set -e

IMAGE=$1
TAG=$2
NO_HEALTH=$3

echo "🚀 Starting deployment..."
echo "Using image: $IMAGE:$TAG"

CURRENT_COLOR=$(docker ps --filter "name=blue-container" --format "{{.Names}}")

if [ "$CURRENT_COLOR" = "blue-container" ]; then
  NEXT_COLOR="green"
else
  NEXT_COLOR="blue"
fi

echo "🔄 Switching to $NEXT_COLOR..."

docker compose -p app-$NEXT_COLOR -f docker-compose.yml up -d \
  --no-deps --build \
  --force-recreate \
  --remove-orphans \
  --scale ${NEXT_COLOR}-container=1

if [ "$NO_HEALTH" != "--no-health" ]; then
  echo "🩺 Checking health..."
  sleep 10
fi

echo "🛑 Stopping old container..."
docker stop app-${CURRENT_COLOR} || true
docker rm app-${CURRENT_COLOR} || true

echo "✅ Deployment finished!"
