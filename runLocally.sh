#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./runLocally.sh            -> build (cached) and run using .env if present or env vars
#   ./runLocally.sh --no-cache -> force rebuild without cache
#   PORT=8081 ./runLocally.sh  -> run container mapping host PORT to container 8080

IMAGE_NAME="${IMAGE_NAME:-dietibackend}"
NO_CACHE=false
PORT="${PORT:-8080}"

if [[ "${1:-}" == "--no-cache" ]]; then
  NO_CACHE=true
fi

echo "Building image ${IMAGE_NAME} (no-cache=${NO_CACHE})..."
if [ "$NO_CACHE" = true ]; then
  docker build --no-cache --tag "$IMAGE_NAME" .
else
  docker build --tag "$IMAGE_NAME" .
fi

# Prepare docker run options
RUN_ARGS=(--rm -p "${PORT}:8080" -v "$(pwd)/logs":/app/logs)

# If a .env file exists, pass it to docker run; otherwise, rely on environment variables.
if [ -f .env ]; then
  echo "Using .env for environment variables"
  RUN_ARGS+=(--env-file .env)
else
  echo "No .env file found. Passing common env vars from the shell (may be empty)."
  RUN_ARGS+=(-e "DB_URL=${DB_URL:-}" \
             -e "DB_USERNAME=${DB_USERNAME:-}" \
             -e "DB_PASSWORD=${DB_PASSWORD:-}" \
             -e "ACCESS_TOKEN_SECRET_KEY=${ACCESS_TOKEN_SECRET_KEY:-}" \
             -e "AZURE_STORAGE_CONNECTION_STRING=${AZURE_STORAGE_CONNECTION_STRING:-}" \
             -e "AZURE_CONTAINER_NAME=${AZURE_CONTAINER_NAME:-}" \
             -e "GEOAPIFY_API_KEY=${GEOAPIFY_API_KEY:-}")
fi

echo "Environment variables passed to Docker:"
for var in "${RUN_ARGS[@]}"; do
  if [[ "$var" == -e* ]]; then
    echo "$var"
  fi
done

# clean up old logs
rm -f logs/*.log

# Run container
docker run "${RUN_ARGS[@]}" "$IMAGE_NAME"