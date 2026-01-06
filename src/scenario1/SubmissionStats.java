package scenario1;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe statistics tracker for submission system
 * Uses AtomicInteger for lock-free concurrent updates
 */
public class SubmissionStats {
    private final AtomicInteger successfulSubmissions;
    private final AtomicInteger failedSubmissions;
    private final AtomicLong startTime;
    private final AtomicLong endTime;

    /**
     * Constructor initializes all counters to zero
     */
    public SubmissionStats() {
        this.successfulSubmissions = new AtomicInteger(0);
        this.failedSubmissions = new AtomicInteger(0);
        this.startTime = new AtomicLong(0);
        this.endTime = new AtomicLong(0);
    }

    /**
     * Record successful submission
     * THREAD SAFETY: AtomicInteger.incrementAndGet() is atomic
     */
    public void recordSuccess() {
        successfulSubmissions.incrementAndGet();
    }

    /**
     * Record failed submission
     * THREAD SAFETY: AtomicInteger.incrementAndGet() is atomic
     */
    public void recordFailure() {
        failedSubmissions.incrementAndGet();
    }

    /**
     * Set start time for performance measurement
     */
    public void setStartTime() {
        startTime.set(System.currentTimeMillis());
    }

    /**
     * Set end time for performance measurement
     */
    public void setEndTime() {
        endTime.set(System.currentTimeMillis());
    }

    /**
     * Get successful submission count
     */
    public int getSuccessfulSubmissions() {
        return successfulSubmissions.get();
    }

    /**
     * Get failed submission count
     */
    public int getFailedSubmissions() {
        return failedSubmissions.get();
    }

    /**
     * Get total submissions processed
     */
    public int getTotalSubmissions() {
        return successfulSubmissions.get() + failedSubmissions.get();
    }

    /**
     * Get total processing time in milliseconds
     */
    public long getTotalTimeMillis() {
        return endTime.get() - startTime.get();
    }

    /**
     * Calculate success rate as percentage
     */
    public double getSuccessRate() {
        int total = getTotalSubmissions();
        if (total == 0) return 0.0;
        return ((double) getSuccessfulSubmissions() / total) * 100;
    }

    /**
     * Calculate throughput (submissions per second)
     */
    public double getThroughput() {
        long timeSeconds = getTotalTimeMillis() / 1000;
        if (timeSeconds == 0) return 0.0;
        return (double) getTotalSubmissions() / timeSeconds;
    }

    /**
     * Display comprehensive statistics
     * Matches UML requirement: printResults(String method, long totalTimeMs)
     */
    public void printResults(String method, long totalTimeMs) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("     SUBMISSION SYSTEM STATISTICS - " + method);
        System.out.println("=".repeat(70));
        System.out.printf("Total Students Processed    : %,d%n", getTotalSubmissions());
        System.out.printf("Successful Submissions      : %,d%n", getSuccessfulSubmissions());
        System.out.printf("Failed Submissions          : %,d%n", getFailedSubmissions());
        System.out.printf("Success Rate                : %.2f%%%n", getSuccessRate());
        System.out.printf("Total Processing Time       : %,d ms (%.2f seconds)%n",
                totalTimeMs, totalTimeMs / 1000.0);
        System.out.printf("Throughput                  : %.2f submissions/second%n", getThroughput());
        System.out.println("=".repeat(70) + "\n");
    }

    /**
     * Display statistics (backward compatibility)
     */
    public void displayStats() {
        printResults("Concurrent Submission", getTotalTimeMillis());
    }

    /**
     * Reset all counters (for testing)
     */
    public void reset() {
        successfulSubmissions.set(0);
        failedSubmissions.set(0);
        startTime.set(0);
        endTime.set(0);
    }
}