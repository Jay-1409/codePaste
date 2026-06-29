# Letterbox

Own your personal pastebin app.

---

## Whats in there for you
- Fully fledged backend system for powering any pastembin application. 
- Store and access text snippets via urls
- Secure pastes with optional password-based access.
- Support paste expiry with configurable duration 
- High performance paste id generation engine (powered with the goods of URL shortner)
- Supports multiple instances (you can scale it as your like)
- Total number of pastes supported 2⁶² − 1 = 4,611,686,018,427,387,903

---

## Installation

### Get the source code
```bash
git clone [https://github.com/Jay-1409/codePaste.git](https://github.com/Jay-1409/codePaste.git)
cd codePaste
```
### Prerequisites 
* Java 17 or above.
* MongoDB.
* Redis.
* Docker and Docker Compose for running Redis with `compose.yaml`.
* Bash shell for running the benchmark script.


### Setting up springboot 

- The project properties are configured in the src/main/resources/application.properties file. You can change the port or database configuration there if needed.

### Setting up Mongodb

- Paste creation and deletion use atomic single-document MongoDB operations, so a local standalone MongoDB instance is supported.
- By default, the application connects to `mongodb://localhost:27017/codepaste`.
- To use MongoDB Atlas, set the `MONGODB_URI` environment variable: `export MONGODB_URI="your-mongodb-atlas-connection-string"`.

### Setting up redis

- Start the Redis container using docker compose: docker compose up -d
- Alternatively, you can run Redis locally on the default port 6379.
- You can configure custom Redis host and port values using the REDIS_HOST and REDIS_PORT environment variables.

### Setting up authentication

The app uses **Basic Authentication**. Default user credentials are:

* **Username:** `user`
* **Password:** `password`

You can customize this in the `SecurityConfig` class.
By default all apis are set to be allowed without authentication.

### Running the project 

- Run the application using the Maven wrapper: ./mvnw spring-boot:run
- Alternatively, you can build the application with: ./mvnw clean package
- Then run the packaged jar file: java -jar target/letterbox-0.0.1-SNAPSHOT.jar


--- 

## Single-replica benchmarks

The following results were measured locally with `one application replica` , `local MongoDB and Redis` , `varied 4 KiB paste payloads` .

| Operation | Concurrency | Requests/sec |
| --- | ---: | ---: |
| Create paste | 10 | 5,079 |
| Create paste | 50 | 5,433 |
| Create paste | 100 | 4,021 |
| Fetch varied pastes | 10 | 2,174 |
| Fetch varied pastes | 50 | 1,607 |
| Fetch varied pastes | 100 | 1,433 |

**These are development-machine baseline results, not production capacity guarantees. The application, databases, and load generator shared the same machine.**

Want to run benchmarks on your system ? [See here](docs/benchmarking.md)

---


## Documentation / FAQ
- Endpoint documentation [here](docs/endpoints.md)
- How the pastebin uid generator engine works? [here](docs/custom-uid-generator.md)
- Whats the architecture of this project [here](docs/architecture.md)
- Whiteboard drawings [here](docs/whiteboard.excalidraw)
