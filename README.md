# Own your personal pastebin app

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
* Java 17 or above installed.
* MongoDB installed and running.
* Maven build tool.


### setting up springboot 

- Make sure you have Java 17 or above installed.
- The project properties are configured in the src/main/resources/application.properties file. You can change the port or database configuration there if needed.

### Setting up Mongodb

- Paste creation and deletion use atomic single-document MongoDB operations, so a local standalone MongoDB instance is supported.
- By default, the application connects to `mongodb://localhost:27017/codepaste`.
- To use MongoDB Atlas, set the `MONGODB_URI` environment variable: `export MONGODB_URI="your-mongodb-atlas-connection-string"`.

### setting up redis

- Start the Redis container using docker compose: docker compose up -d
- Alternatively, you can run Redis locally on the default port 6379.
- You can configure custom Redis host and port values using the REDIS_HOST and REDIS_PORT environment variables.

### Running the project 

- Run the application using the Maven wrapper: ./mvnw spring-boot:run
- Alternatively, you can build the application with: ./mvnw clean package
- Then run the packaged jar file: java -jar target/codePost-0.0.1-SNAPSHOT.jar

## Single-replica benchmarks

The following results were measured locally with one application replica, local MongoDB and Redis, a small public paste payload, and 15-second runs after warm-up.

| Operation | Concurrency | Requests/sec | p95 latency | p99 latency | Errors |
| --- | ---: | ---: | ---: | ---: | ---: |
| Create paste | 10 | 3,893 | 4.6 ms | 7.9 ms | 0 |
| Create paste | 50 | 5,745 | 21.8 ms | 38.3 ms | 0 |
| Create paste | 100 | 4,492 | 63.7 ms | 108.1 ms | 0 |
| Fetch cached paste | 10 | 7,480 | 2.1 ms | 3.0 ms | 0 |
| Fetch cached paste | 50 | 10,267 | 7.7 ms | 11.7 ms | 0 |
| Fetch cached paste | 100 | 11,786 | 15.0 ms | 27.5 ms | 0 |

Paste creation reached its best measured throughput at concurrency 50. Cached reads were still scaling at concurrency 100. These are development-machine baseline results, not production capacity guarantees. The application, databases, and load generator shared the same machine; network latency, larger payloads, password hashing, and production infrastructure may change the results.


### Setting up authentication

The app uses **Basic Authentication**. Default user credentials are:

* **Username:** `user`
* **Password:** `password`

You can customize this in the `SecurityConfig` class.
By default all apis are set to be allowed without authentication.

---


## Documentation / FAQ
- Endpoint documentation here []()
- How the pastebin uid generator engine works? []()
- Whats the architecture of this project []()
- Whiteboard drawings []()
