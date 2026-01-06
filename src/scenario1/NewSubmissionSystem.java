package scenario1;

import java.util.concurrent.*;

/**
 * Concurrent submission system that processes student submissions in parallel
 * Replaces old sequential system that caused 20-30 minute wait times
 *
 * CONCURRENCY PATTERN: Thread Pool with CountDownLatch coordination
 */
public class NewSubmissionSystem {
    private final int numberOfStudents;
    private final int poolSize;
    private final SubmissionStats stats;
    private final ExecutorService executor;

    /**
     * Constructor
     * @param poolSize Number of threads in pool (typically 2× CPU cores)
     * @param numberOfStudents Total students submitting
     */
    public NewSubmissionSystem(int poolSize, int numberOfStudents) {
        this.poolSize = poolSize;
        this.numberOfStudents = numberOfStudents;
        this.stats = new SubmissionStats();
        this.executor = Executors.newFixedThreadPool(poolSize);

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   NEW CONCURRENT SUBMISSION SYSTEM INITIALIZED         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.printf("Thread Pool Size: %d%n", poolSize);
        System.out.printf("Target Capacity: %,d students%n", numberOfStudents);
        System.out.println("-".repeat(60) + "\n");
    }

    /**
     * Process all submissions concurrently
     *
     * CRITICAL FIX: Each student submission is a SEPARATE task
     * submitted to thread pool for concurrent execution
     *
     * ORIGINAL BUG: Lecturer's code had one task with sequential loop
     */
    public void processSubmissions() {
        System.out.printf("Processing %,d student submissions concurrently...%n%n", numberOfStudents);

        stats.setStartTime();

        // CountDownLatch: Wait for all submissions to complete
        CountDownLatch latch = new CountDownLatch(numberOfStudents);

        // ✅ CORRECT: Submit EACH student as separate concurrent task
        for (int i = 1; i <= numberOfStudents; i++) {
            final int studentId = i;
            final String studentName = "Student_" + i;

            // Each submit() call adds task to pool for concurrent execution
            executor.submit(() -> {
                try {
                    // Create student and process submission
                    Student student = new Student(studentId, studentName);
                    boolean success = student.submitExam();

                    // Record result
                    if (success) {
                        stats.recordSuccess();
                    } else {
                        stats.recordFailure();
                        // Only log failures to reduce output volume
                        System.out.printf("✗ %s submission failed (timeout/error)%n", studentName);
                    }

                } catch (InterruptedException e) {
                    stats.recordFailure();
                    System.err.printf("✗ %s submission interrupted%n", studentName);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    stats.recordFailure();
                    System.err.printf("✗ %s submission error: %s%n", studentName, e.getMessage());
                } finally {
                    // Always count down, even if exception
                    latch.countDown();
                }
            });
        }

        // Wait for all submissions to complete
        try {
            System.out.println("⏳ Waiting for all submissions to complete...\n");
            latch.await();  // Blocks until latch reaches 0
            stats.setEndTime();
            System.out.println("\n✓ All submissions processed!");
        } catch (InterruptedException e) {
            System.err.println("Submission processing interrupted!");
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
     * Shutdown executor service properly
     * RESOURCE MANAGEMENT: Always call this to prevent thread leaks
     */
    public void shutdown() throws InterruptedException {
        System.out.println("\nShutting down submission system...");
        executor.shutdown();

        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("Timeout waiting for shutdown, forcing...");
            executor.shutdownNow();

            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate!");
            }
        }

        System.out.println("✓ Submission system shutdown complete.\n");
    }

    /**
     * Getters
     */
    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public int getPoolSize() {
        return poolSize;
    }
}