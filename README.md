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
