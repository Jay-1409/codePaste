# Benchmarking Guide
_Currenly the tool only supports single replica benchmarking_

## Running the benchmark

1. Start MongoDB.
2. Start Redis with `docker compose up -d`.
3. Run the benchmark:

```bash
./scripts/benchmark-single-replica.sh
```

## How it works

1. The script starts one application replica on port `18080`.
2. It uses a temporary MongoDB database and Redis database `15`.
3. It prepares 10,000 different pastes of approximately 4 KiB each.
4. It sends a few warm-up requests before collecting results.
5. It creates pastes at concurrency levels `10`, `50`, and `100`.
6. Every returned paste ID is stored in memory.
7. It fetches the stored paste IDs in round-robin order.
8. Every paste ID is fetched at least three times.
9. It prints requests per second, latency, errors, and HTTP status codes.
10. It stops the application and removes the temporary benchmark data.

## What concurrency means

Concurrency is the number of requests active at the same time.

- Concurrency `10` keeps approximately 10 requests active.
- Concurrency `50` keeps approximately 50 requests active.
- Concurrency `100` keeps approximately 100 requests active.

When one request finishes, the benchmark immediately sends another one. Concurrency is not the same as requests per second.

## Changing the benchmark

For a shorter and lighter run:

```bash
DURATION=5s CONCURRENCIES="10 25 50" FETCH_REPEATS=2 ./scripts/benchmark-single-replica.sh
```
