# Letterbox Installation Guide

## Get the source code

```bash
git clone https://github.com/Jay-1409/codePaste.git
cd codePaste
```

## Prerequisites

- Java 17 or above
- MongoDB
- Redis
- Docker and Docker Compose for running Redis with `compose.yaml`
- Bash for running the benchmark script

## Application configuration

Application settings are stored in:

```text
src/main/resources/application.properties
```

The application runs on port `8080` by default.

## MongoDB

Paste creation and deletion use atomic single-document operations, so a local standalone MongoDB instance is supported.

The default connection is:

```text
mongodb://localhost:27017/codepaste
```

To use MongoDB Atlas, set `MONGODB_URI` before starting Letterbox:

```bash
export MONGODB_URI="your-mongodb-atlas-connection-string"
```

## Redis

Start the configured Redis container:

```bash
docker compose up -d
```

You can instead run Redis locally on port `6379`.

For a different Redis server, set:

```bash
export REDIS_HOST="your-redis-host"
export REDIS_PORT="6379"
```

## Run Letterbox

Run the application with the Maven wrapper:

```bash
sh ./mvnw spring-boot:run
```

Alternatively, build and run the JAR:

```bash
sh ./mvnw clean package
java -jar target/letterbox-0.0.1-SNAPSHOT.jar
```

## Authentication

Basic Authentication is configured with these default credentials:

- Username: `user`
- Password: `password`

All API endpoints are currently configured with `permitAll`, so credentials are not required. Authentication settings can be changed in `SecurityConfig`.
