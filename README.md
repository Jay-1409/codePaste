# codePaste - Pastebin Application

---

## Overview

codePaste is a Pastebin-like application designed to demonstrate **system design skills**, focusing on building a scalable, maintainable, and efficient backend service. This project showcases how to handle data persistence, caching, security, and API design in a real-world scenario.
---

## Purpose

This project is primarily a demonstration of system design capabilities including:

* Designing **RESTful APIs** for creating, retrieving, and managing text pastes.
* Implementing **security with authentication** and access controls.
* Managing **data storage with MongoDB** for flexibility and scalability.
* Leveraging **caching mechanisms** to improve performance.
* Handling paste expiration and password protection features.
* Applying **clean architecture** and separation of concerns.

---

## Features

* **Create & Retrieve Pastes:** Store and access text snippets via unique paste IDs.
* **Password Protection:** Secure pastes with optional password-based access.
* **Expiration:** Support paste expiry after a configurable duration.
* **Access Control:** Public and private pastes with access restrictions.
* **Caching:** Performance optimization via caching frequently accessed data.
* **Basic Authentication:** Secure endpoints with HTTP Basic Auth.
* **RESTful API:** Well-structured API design to facilitate client integrations.

---

## Tech Stack

* **Backend:** Java, Spring Boot
* **Database:** MongoDB
* **Security:** Spring Security (Basic Auth, BCrypt password encoding)
* **Caching:** Spring Cache abstraction (e.g., with Redis or in-memory)
* **Build & Dependency:** Maven
* **Testing:** Postman (for API testing and visualization)

---

## System Design Highlights

* Modular service and controller layers to separate business logic.
* Use of **caching annotations** (`@Cacheable`, `@CacheEvict`, `@CachePut`) to balance consistency and performance.
* Thoughtful **data modeling** with MongoDB documents for pastes.
* **Secure password storage** using BCrypt hashing.
* **Expiry mechanism** with timestamp checks and background cleanup.
* **Selective authentication** configuration allowing public and protected endpoints.

---
### Project Structure
![design1](https://github.com/user-attachments/assets/ebd4599c-193a-4a65-879d-d7ca331749ce)

---
## Getting Started

### Prerequisites

* Java 17 or above installed.
* MongoDB installed and running.
* Maven build tool.

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/Jay-1409/codePaste.git](https://github.com/Jay-1409/codePaste.git)
    cd codePaste
    ```
2.  **Configure MongoDB:**
    Update `src/main/resources/application.properties` with your MongoDB URI:
    ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/codepaste
    ```
3.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```
4.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```

### Authentication

The app uses **Basic Authentication**. Default user credentials are:

* **Username:** `user`
* **Password:** `password`

You can customize this in the `SecurityConfig` class.
By default all apis are set to be allowed without authentication.
### API Endpoints

* `POST /paste/addPaste` — Create a new paste.
* `GET /paste/{pasteId}` — Retrieve a paste by its ID.
* `DELETE /paste/deletePaste?pasteId=xxx` — Delete a paste by ID.

*Additional endpoints are secured except those explicitly configured to be public.*

---

## Future Improvements

* Implement user registration and paste ownership.
* Add richer metadata and search functionality.
* Integrate rate limiting and abuse prevention.
* Provide a frontend client for better UX.

---

## Contribution

This repository is mainly for demonstration and learning purposes. Feel free to fork and experiment.

---

## License

This project is open-source and free to use.

---

Built with ❤️ by Jay-1409
