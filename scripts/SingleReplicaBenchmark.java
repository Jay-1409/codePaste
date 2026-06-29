import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class SingleReplicaBenchmark {
    private static final int PAYLOAD_POOL_SIZE = 10_000;
    private static final int PASTE_LENGTH = 4_096;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final String baseUrl;
    private final Duration measuredDuration;
    private final Duration warmupDuration;
    private final List<Integer> concurrencies;
    private final int minimumFetchesPerId;
    private final List<String> requestBodies;
    private final List<String> pasteIds = new ArrayList<>();
    private final AtomicLong createSequence = new AtomicLong();
    private final AtomicLong readSequence = new AtomicLong();
    private final ExecutorService executor;
    private final HttpClient client;

    private SingleReplicaBenchmark(
            String baseUrl,
            Duration measuredDuration,
            Duration warmupDuration,
            List<Integer> concurrencies,
            int minimumFetchesPerId
    ) {
        this.baseUrl = baseUrl;
        this.measuredDuration = measuredDuration;
        this.warmupDuration = warmupDuration;
        this.concurrencies = concurrencies;
        this.minimumFetchesPerId = minimumFetchesPerId;
        this.requestBodies = buildRequestBodies();

        int maximumConcurrency = concurrencies.stream().mapToInt(Integer::intValue).max().orElse(100);
        this.executor = Executors.newFixedThreadPool(maximumConcurrency + 4);
        this.client = HttpClient.newBuilder()
                .executor(executor)
                .connectTimeout(REQUEST_TIMEOUT)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.err.println("Usage: java SingleReplicaBenchmark.java <base-url> <duration> <warmup-duration> <concurrency-csv> <minimum-fetches-per-id>");
            System.exit(2);
        }

        SingleReplicaBenchmark benchmark = new SingleReplicaBenchmark(
                removeTrailingSlash(args[0]),
                parseDuration(args[1]),
                parseDuration(args[2]),
                parseConcurrencies(args[3]),
                parsePositiveInt(args[4], "Minimum fetches per ID")
        );

        try {
            benchmark.run();
        } finally {
            benchmark.executor.shutdownNow();
        }
    }

    private void run() throws InterruptedException {
        System.out.printf(
                "Prepared %,d mutated request bodies of approximately %,d characters each.%n",
                requestBodies.size(),
                PASTE_LENGTH
        );

        System.out.printf("%nWarming up paste creation for %s at concurrency 10%n", formatDuration(warmupDuration));
        PhaseResult createWarmup = runCreatePhase(10, warmupDuration);
        pasteIds.addAll(createWarmup.pasteIds());

        for (int concurrency : concurrencies) {
            System.out.printf(
                    "%n=== Create varied pastes: concurrency %d, duration %s ===%n",
                    concurrency,
                    formatDuration(measuredDuration)
            );
            PhaseResult result = runCreatePhase(concurrency, measuredDuration);
            pasteIds.addAll(result.pasteIds());
            printResult(result);
        }

        if (pasteIds.isEmpty()) {
            throw new IllegalStateException("No paste IDs were returned by successful create requests");
        }

        System.out.printf("%nStored %,d returned paste IDs in memory.%n", pasteIds.size());
        System.out.printf("Warming up varied paste reads for %s at concurrency 10%n", formatDuration(warmupDuration));
        runFetchPhase(10, warmupDuration);

        long measuredReads = 0;
        for (int concurrency : concurrencies) {
            System.out.printf(
                    "%n=== Fetch varied cached pastes: concurrency %d, duration %s ===%n",
                    concurrency,
                    formatDuration(measuredDuration)
            );
            PhaseResult result = runFetchPhase(concurrency, measuredDuration);
            measuredReads += result.completed();
            printResult(result);
        }

        long requiredFetches = (long) pasteIds.size() * minimumFetchesPerId;
        long completedFetches = readSequence.get();
        if (completedFetches < requiredFetches) {
            long remainingFetches = requiredFetches - completedFetches;
            int concurrency = concurrencies.stream().mapToInt(Integer::intValue).max().orElse(100);
            System.out.printf(
                    "%n=== Fetch coverage: %,d additional requests at concurrency %d ===%n",
                    remainingFetches,
                    concurrency
            );
            PhaseResult coverageResult = runFetchCount(concurrency, remainingFetches);
            printResult(coverageResult);
        }

        System.out.printf(
                "%nCompleted %,d measured fetches across %,d paste IDs.%n",
                measuredReads,
                pasteIds.size()
        );
        System.out.printf(
                "Including warm-up and coverage, every ID was fetched at least %,d times.%n",
                readSequence.get() / pasteIds.size()
        );
    }

    private PhaseResult runCreatePhase(int concurrency, Duration duration) throws InterruptedException {
        return runPhase(concurrency, duration, sequence -> {
            String body = requestBodies.get(Math.floorMod(sequence, requestBodies.size()));
            return HttpRequest.newBuilder(URI.create(baseUrl + "/paste/addPaste"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
        }, createSequence, true);
    }

    private PhaseResult runFetchPhase(int concurrency, Duration duration) throws InterruptedException {
        return runPhase(concurrency, duration, this::buildFetchRequest, readSequence, false);
    }

    private PhaseResult runFetchCount(int concurrency, long requestCount) throws InterruptedException {
        int workersToStart = (int) Math.min(concurrency, requestCount);
        long startedAt = System.nanoTime();
        CountDownLatch completion = new CountDownLatch(workersToStart);
        AtomicLong issuedRequests = new AtomicLong();
        List<WorkerResult> workers = new ArrayList<>(workersToStart);

        for (int workerNumber = 0; workerNumber < workersToStart; workerNumber++) {
            WorkerResult workerResult = new WorkerResult();
            workers.add(workerResult);
            sendNextCount(workerResult, requestCount, issuedRequests, completion);
        }

        completion.await();
        return combine(workers, System.nanoTime() - startedAt);
    }

    private HttpRequest buildFetchRequest(long sequence) {
        String pasteId = pasteIds.get(Math.floorMod(sequence, pasteIds.size()));
        return HttpRequest.newBuilder(URI.create(baseUrl + "/paste/getPaste?pasteId=" + pasteId))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
    }

    private PhaseResult runPhase(
            int concurrency,
            Duration duration,
            RequestFactory requestFactory,
            AtomicLong sequence,
            boolean capturePasteIds
    ) throws InterruptedException {
        long startedAt = System.nanoTime();
        long deadline = startedAt + duration.toNanos();
        CountDownLatch completion = new CountDownLatch(concurrency);
        List<WorkerResult> workers = new ArrayList<>(concurrency);

        for (int workerNumber = 0; workerNumber < concurrency; workerNumber++) {
            WorkerResult workerResult = new WorkerResult();
            workers.add(workerResult);
            sendNext(workerResult, deadline, completion, requestFactory, sequence, capturePasteIds);
        }

        completion.await();
        long elapsedNanos = System.nanoTime() - startedAt;
        return combine(workers, elapsedNanos);
    }

    private void sendNext(
            WorkerResult result,
            long deadline,
            CountDownLatch completion,
            RequestFactory requestFactory,
            AtomicLong sequence,
            boolean capturePasteIds
    ) {
        if (System.nanoTime() >= deadline) {
            completion.countDown();
            return;
        }

        long requestNumber = sequence.getAndIncrement();
        HttpRequest request = requestFactory.create(requestNumber);
        long requestStartedAt = System.nanoTime();

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        responseFuture.whenComplete((response, failure) -> {
            result.latenciesNanos.add(System.nanoTime() - requestStartedAt);
            recordResponse(result, response, failure, capturePasteIds);

            sendNext(result, deadline, completion, requestFactory, sequence, capturePasteIds);
        });
    }

    private void sendNextCount(
            WorkerResult result,
            long requestCount,
            AtomicLong issuedRequests,
            CountDownLatch completion
    ) {
        if (issuedRequests.getAndIncrement() >= requestCount) {
            completion.countDown();
            return;
        }

        HttpRequest request = buildFetchRequest(readSequence.getAndIncrement());
        long requestStartedAt = System.nanoTime();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .whenComplete((response, failure) -> {
                    result.latenciesNanos.add(System.nanoTime() - requestStartedAt);
                    recordResponse(result, response, failure, false);
                    sendNextCount(result, requestCount, issuedRequests, completion);
                });
    }

    private static void recordResponse(
            WorkerResult result,
            HttpResponse<String> response,
            Throwable failure,
            boolean capturePasteIds
    ) {
        result.completed++;
        if (failure != null) {
            result.errors++;
            return;
        }

        result.statusCodes.merge(response.statusCode(), 1L, Long::sum);
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            result.errors++;
        } else if (capturePasteIds) {
            String pasteId = extractPasteId(response.body());
            if (pasteId == null) {
                result.errors++;
            } else {
                result.pasteIds.add(pasteId);
            }
        }
    }

    private static PhaseResult combine(List<WorkerResult> workers, long elapsedNanos) {
        long completed = 0;
        long errors = 0;
        List<Long> latencies = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        Map<Integer, Long> statuses = new LinkedHashMap<>();

        for (WorkerResult worker : workers) {
            completed += worker.completed;
            errors += worker.errors;
            latencies.addAll(worker.latenciesNanos);
            ids.addAll(worker.pasteIds);
            worker.statusCodes.forEach((status, count) -> statuses.merge(status, count, Long::sum));
        }

        Collections.sort(latencies);
        return new PhaseResult(completed, errors, elapsedNanos, latencies, ids, statuses);
    }

    private static void printResult(PhaseResult result) {
        double elapsedSeconds = result.elapsedNanos() / 1_000_000_000.0;
        System.out.printf("Completed:     %,d%n", result.completed());
        System.out.printf("Requests/sec:  %,.2f%n", result.completed() / elapsedSeconds);
        System.out.printf("p50 latency:   %.2f ms%n", percentileMillis(result.latenciesNanos(), 0.50));
        System.out.printf("p95 latency:   %.2f ms%n", percentileMillis(result.latenciesNanos(), 0.95));
        System.out.printf("p99 latency:   %.2f ms%n", percentileMillis(result.latenciesNanos(), 0.99));
        System.out.printf("Errors:        %,d%n", result.errors());
        System.out.printf("Status codes:  %s%n", result.statusCodes());
    }

    private static double percentileMillis(List<Long> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) {
            return 0;
        }
        int index = Math.max(0, (int) Math.ceil(sortedValues.size() * percentile) - 1);
        return sortedValues.get(index) / 1_000_000.0;
    }

    private static List<String> buildRequestBodies() {
        String repeatedText = "The quick brown fox jumps over the lazy dog 0123456789. ".repeat(100);
        List<String> bodies = new ArrayList<>(PAYLOAD_POOL_SIZE);

        for (int mutation = 0; mutation < PAYLOAD_POOL_SIZE; mutation++) {
            String marker = String.format(" [mutation-%05d]", mutation);
            String paste = repeatedText.substring(0, PASTE_LENGTH - marker.length()) + marker;
            bodies.add("{\"paste\":\"" + paste + "\",\"expireAfter\":7,\"access\":true}");
        }

        return List.copyOf(bodies);
    }

    private static String extractPasteId(String responseBody) {
        String prefix = "\"pasteId\":\"";
        int valueStart = responseBody.indexOf(prefix);
        if (valueStart < 0) {
            return null;
        }
        valueStart += prefix.length();
        int valueEnd = responseBody.indexOf('"', valueStart);
        return valueEnd < 0 ? null : responseBody.substring(valueStart, valueEnd);
    }

    private static Duration parseDuration(String value) {
        if (value.endsWith("ms")) {
            return Duration.ofMillis(Long.parseLong(value.substring(0, value.length() - 2)));
        }
        if (value.endsWith("s")) {
            return Duration.ofSeconds(Long.parseLong(value.substring(0, value.length() - 1)));
        }
        return Duration.ofSeconds(Long.parseLong(value));
    }

    private static List<Integer> parseConcurrencies(String value) {
        List<Integer> parsed = new ArrayList<>();
        for (String item : value.split(",")) {
            int concurrency = Integer.parseInt(item.trim());
            if (concurrency <= 0) {
                throw new IllegalArgumentException("Concurrency must be greater than zero");
            }
            parsed.add(concurrency);
        }
        return List.copyOf(parsed);
    }

    private static int parsePositiveInt(String value, String label) {
        int parsed = Integer.parseInt(value);
        if (parsed <= 0) {
            throw new IllegalArgumentException(label + " must be greater than zero");
        }
        return parsed;
    }

    private static String removeTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private static String formatDuration(Duration duration) {
        return duration.toMillis() + "ms";
    }

    @FunctionalInterface
    private interface RequestFactory {
        HttpRequest create(long sequence);
    }

    private static final class WorkerResult {
        private long completed;
        private long errors;
        private final List<Long> latenciesNanos = new ArrayList<>();
        private final List<String> pasteIds = new ArrayList<>();
        private final Map<Integer, Long> statusCodes = new HashMap<>();
    }

    private record PhaseResult(
            long completed,
            long errors,
            long elapsedNanos,
            List<Long> latenciesNanos,
            List<String> pasteIds,
            Map<Integer, Long> statusCodes
    ) {
    }
}
