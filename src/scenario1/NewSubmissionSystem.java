package scenario1;

import java.util.concurrent.*;
import java.util.Random;

/**
 * Main submission system that processes student submissions concurrently
 * Uses thread pool to handle thousands of simultaneous submissions
 */
public class NewSubmissionSystem {

    private final SubmissionStats stats;
    private final ExecutorService executorService;
    private final Random random;

    // Configuration
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MIN_PROCESSING_TIME_MS = 50;
    private static final int MAX_PROCESSING_TIME_MS = 200;
    private static final double FAILURE_RATE = 0.05; // 5% failure rate

    /**
     * Constructor initializes the system
     */
    public NewSubmissionSystem() {
        this.stats = new SubmissionStats();
        // Fixed thread pool: reuses threads, efficient for many tasks
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.random = new Random();

        System.out.println("Submission System Initialized");
        System.out.println("Thread Pool Size: " + THREAD_POOL_SIZE);
        System.out.println("-".repeat(60));
    }

    /**
     * Process submissions for given number of students
     * Uses CountDownLatch to wait for all threads to complete
     */
    public void processSubmissions(int numberOfStudents) {
        System.out.println("\nProcessing " + numberOfStudents + " student submissions...\n");

        long startTime = System.currentTimeMillis();

        // CountDownLatch: counts down from numberOfStudents to 0
        // Main thread waits until count reaches 0
        CountDownLatch latch = new CountDownLatch(numberOfStudents);

        // Submit all tasks to thread pool
        for (int i = 1; i <= numberOfStudents; i++) {
            final int studentId = i;

            // Submit task to executor
            executorService.submit(() -> {
                try {
                    processIndividualSubmission(studentId);
                } catch (Exception e) {
                    System.err.println("Error processing student " + studentId + ": " + e.getMessage());
                    stats.incrementFailure();
                } finally {
                    // Always count down, even if exception occurs
                    latch.countDown();
                }
            });
        }

        // Wait for all submissions to complete
        try {
            System.out.println("Waiting for all submissions to complete...\n");
            latch.await(); // Blocks until latch count reaches 0

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            stats.setTotalProcessingTime(totalTime);

            System.out.println("\nAll submissions processed!");

        } catch (InterruptedException e) {
            System.err.println("Submission processing interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Simulates processing a single student's submission
     * This method runs in a worker thread
     */
    private void processIndividualSubmission(int studentId) {
        try {
            // Simulate variable processing time
            int processingTime = MIN_PROCESSING_TIME_MS +
                    random.nextInt(MAX_PROCESSING_TIME_MS - MIN_PROCESSING_TIME_MS);
            Thread.sleep(processingTime);

            // Simulate random failures (network issues, file corruption, etc.)
            boolean success = random.nextDouble() > FAILURE_RATE;

            if (success) {
                stats.incrementSuccess();
                // Uncomment to see individual success messages (verbose for large numbers)
                // System.out.println("✓ Student " + studentId + " submission successful");
            } else {
                stats.incrementFailure();
                System.out.println("✗ Student " + studentId + " submission failed (timeout/error)");
            }

        } catch (InterruptedException e) {
            System.err.println("Submission interrupted for student " + studentId);
            stats.incrementFailure();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Display final statistics
     */
    public void displayResults() {
        stats.displayStats();
    }

    /**
     * Shutdown the executor service properly
     * Important: Always call this to release resources
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}