# Letterbox

Letterbox is a ready-to-run paste service for applications, developer tools, Discord bots, Telegram bots, and automation workflows.
Add shareable paste links, custom IDs, password protection, and automatic expiration without building a paste backend from scratch.
Integrate through simple HTTP APIs, deploy it yourself, and scale with MongoDB and Redis as your project grows.

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

Follow the [installation guide](docs/installation.md).

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
- Installation guide [here](docs/installation.md)
- Endpoint documentation [here](docs/endpoints.md)
- How the pastebin uid generator engine works? [here](docs/custom-uid-generator.md)
- Whats the architecture of this project [here](docs/architecture.md)
- Whiteboard drawings [here](docs/whiteboard.excalidraw)
