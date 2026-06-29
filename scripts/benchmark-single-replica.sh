#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
PROJECT_DIR=$(cd -- "${SCRIPT_DIR}/.." && pwd)
cd "${PROJECT_DIR}"

PORT="${PORT:-18080}"
BASE_URL="${BASE_URL:-http://127.0.0.1:${PORT}}"
DURATION="${DURATION:-15s}"
WARMUP_DURATION="${WARMUP_DURATION:-5s}"
CONCURRENCIES="${CONCURRENCIES:-10 50 100}"
FETCH_REPEATS="${FETCH_REPEATS:-3}"
REDIS_DATABASE="${REDIS_DATABASE:-15}"
BENCHMARK_MONGODB_URI="${BENCHMARK_MONGODB_URI:-mongodb://localhost:27017/letterbox_benchmark_$$}"
APP_LOG="${TMPDIR:-/tmp}/letterbox-benchmark-$$.log"

APP_PID=""
REDIS_PREPARED=false

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Required command not found: $1" >&2
    exit 1
  fi
}

redis_command() {
  if command -v redis-cli >/dev/null 2>&1; then
    redis-cli -n "${REDIS_DATABASE}" "$@"
  else
    docker compose exec -T redis redis-cli -n "${REDIS_DATABASE}" "$@"
  fi
}

cleanup() {
  local exit_code=$?
  trap - EXIT INT TERM

  if [[ -n "${APP_PID}" ]] && kill -0 "${APP_PID}" >/dev/null 2>&1; then
    kill "${APP_PID}" >/dev/null 2>&1 || true
    wait "${APP_PID}" >/dev/null 2>&1 || true
  fi

  if [[ "${REDIS_PREPARED}" == true ]]; then
    redis_command FLUSHDB >/dev/null 2>&1 || true
  fi

  mongosh "${BENCHMARK_MONGODB_URI}" --quiet --eval 'db.dropDatabase()' >/dev/null 2>&1 || true

  if [[ ${exit_code} -ne 0 ]]; then
    echo "Benchmark failed. Application log: ${APP_LOG}" >&2
  else
    rm -f "${APP_LOG}"
  fi

  exit "${exit_code}"
}

trap cleanup EXIT INT TERM

require_command curl
require_command java
require_command mongosh

if ! command -v redis-cli >/dev/null 2>&1; then
  require_command docker
fi

if curl --silent --fail "${BASE_URL}/paste/system" >/dev/null 2>&1; then
  echo "Port ${PORT} is already serving an application. Choose another PORT." >&2
  exit 1
fi

if ! redis_command PING | grep -q PONG; then
  echo "Redis is unavailable. Start it with: docker compose up -d redis" >&2
  exit 1
fi

if ! mongosh "${BENCHMARK_MONGODB_URI}" --quiet --eval 'db.runCommand({ ping: 1 }).ok' | grep -q 1; then
  echo "MongoDB is unavailable at ${BENCHMARK_MONGODB_URI}" >&2
  exit 1
fi

if [[ "$(redis_command DBSIZE)" != "0" ]]; then
  echo "Redis database ${REDIS_DATABASE} is not empty; refusing to delete its data." >&2
  echo "Choose an empty database with REDIS_DATABASE=<number>." >&2
  exit 1
fi

REDIS_PREPARED=true

echo "Starting one application replica on ${BASE_URL}"
echo "MongoDB: ${BENCHMARK_MONGODB_URI}"
echo "Redis database: ${REDIS_DATABASE}"

MONGODB_URI="${BENCHMARK_MONGODB_URI}" \
SPRING_DATA_REDIS_DATABASE="${REDIS_DATABASE}" \
SERVER_PORT="${PORT}" \
sh ./mvnw spring-boot:run >"${APP_LOG}" 2>&1 &
APP_PID=$!

for _ in {1..60}; do
  if curl --silent --fail "${BASE_URL}/paste/system" >/dev/null 2>&1; then
    break
  fi
  if ! kill -0 "${APP_PID}" >/dev/null 2>&1; then
    echo "Application stopped during startup." >&2
    exit 1
  fi
  sleep 1
done

if ! curl --silent --fail "${BASE_URL}/paste/system" >/dev/null 2>&1; then
  echo "Application did not become healthy within 60 seconds." >&2
  exit 1
fi

echo
CONCURRENCY_CSV=${CONCURRENCIES// /,}
java "${SCRIPT_DIR}/SingleReplicaBenchmark.java" \
  "${BASE_URL}" \
  "${DURATION}" \
  "${WARMUP_DURATION}" \
  "${CONCURRENCY_CSV}" \
  "${FETCH_REPEATS}"

echo
echo "Benchmark complete. The test replica will stop and isolated data will be removed."
