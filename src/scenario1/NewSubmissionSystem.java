package scenario1;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

/**
 * New concurrent submission system that replaces the old sequential system
 * Handles thousands of concurrent student submissions efficiently
 */
public class NewSubmissionSystem {

    private final SubmissionStats stats;
    private final ExecutorService executorService;
    private final int poolSize;

    /**
     * Constructor - initializes thread pool
     */
    public NewSubmissionSystem(int numberOfStudents, int poolSize) {
        this.stats = new SubmissionStats();
        this.poolSize = poolSize;
        this.executorService = Executors.newFixedThreadPool(poolSize);

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   NEW CONCURRENT SUBMISSION SYSTEM INITIALIZED         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("Thread Pool Size: " + poolSize);
        System.out.println("Target Capacity: " + numberOfStudents + " students");
        System.out.println("-".repeat(60) + "\n");
    }

    /**
     * Process all student submissions concurrently
     */
    public void processSubmissions(int numberOfStudents) {
        System.out.println("Processing " + numberOfStudents + " student submissions...\n");

        long startTime = System.currentTimeMillis();

        // Create all students
        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= numberOfStudents; i++) {
            students.add(new Student(i));
        }

        // CountDownLatch to wait for all submissions
        CountDownLatch latch = new CountDownLatch(numberOfStudents);

        // Submit all student tasks to thread pool
        for (Student student : students) {
            executorService.submit(() -> {
                try {
                    processStudentSubmission(student);
                } catch (Exception e) {
                    System.err.println("Error processing " + student.getName() + ": " + e.getMessage());
                    stats.recordFailure();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all submissions to complete
        try {
            System.out.println("⏳ Waiting for all submissions to complete...\n");
            latch.await(); // Block until all threads finish

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            System.out.println("\n✓ All submissions processed!");

            // Display results using UML method signature
            stats.printResults("Concurrent Submission", totalTime);

        } catch (InterruptedException e) {
            System.err.println("Submission processing interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Process individual student's submission
     */
    private void processStudentSubmission(Student student) {
        // Call student's submitExam() method
        String result = student.submitExam();

        // Record result based on return value
        switch (result) {
            case "SUCCESS":
                stats.recordSuccess();
                // Uncomment for verbose output:
                // System.out.println("✓ " + student.getName() + " - Submission successful");
                break;

            case "FAILED":
                stats.recordFailure();
                System.out.println("✗ " + student.getName() + " - Submission failed (timeout/error)");
                break;

            case "ERROR":
                stats.recordFailure();
                System.out.println("✗ " + student.getName() + " - Submission error (interrupted)");
                break;
        }
    }

    /**
     * Shutdown the executor service
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
        System.out.println("System shutdown complete.\n");
    }
}