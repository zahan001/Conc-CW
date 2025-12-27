package scenario1;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe class to track submission statistics
 * Uses atomic variables to handle concurrent updates from multiple threads
 */
public class SubmissionStats {

    // AtomicInteger ensures thread-safe increment operations
    private final AtomicInteger successfulSubmissions;
    private final AtomicInteger failedSubmissions;
    private final AtomicLong totalProcessingTime;

    public SubmissionStats() {
        this.successfulSubmissions = new AtomicInteger(0);
        this.failedSubmissions = new AtomicInteger(0);
        this.totalProcessingTime = new AtomicLong(0);
    }

    /**
     * Increment successful submission counter
     * Thread-safe: Multiple threads can call this simultaneously
     */
    public void incrementSuccess() {
        successfulSubmissions.incrementAndGet();
    }

    /**
     * Increment failed submission counter
     * Thread-safe: Uses atomic operation
     */
    public void incrementFailure() {
        failedSubmissions.incrementAndGet();
    }

    /**
     * Get total submissions processed
     */
    public int getTotalSubmissions() {
        return successfulSubmissions.get() + failedSubmissions.get();
    }

    /**
     * Get successful submissions count
     */
    public int getSuccessfulSubmissions() {
        return successfulSubmissions.get();
    }

    /**
     * Get failed submissions count
     */
    public int getFailedSubmissions() {
        return failedSubmissions.get();
    }

    /**
     * Calculate success rate as percentage
     */
    public double getSuccessRate() {
        int total = getTotalSubmissions();
        if (total == 0) return 0.0;
        return (successfulSubmissions.get() * 100.0) / total;
    }

    /**
     * Set total processing time
     */
    public void setTotalProcessingTime(long milliseconds) {
        totalProcessingTime.set(milliseconds);
    }

    /**
     * Display comprehensive statistics
     * Synchronized to ensure consistent snapshot of all values
     */
    public synchronized void displayStats() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           SUBMISSION SYSTEM STATISTICS");
        System.out.println("=".repeat(60));
        System.out.printf("Total Students Processed    : %,d%n", getTotalSubmissions());
        System.out.printf("Successful Submissions      : %,d%n", getSuccessfulSubmissions());
        System.out.printf("Failed Submissions          : %,d%n", getFailedSubmissions());
        System.out.printf("Success Rate                : %.2f%%%n", getSuccessRate());
        System.out.printf("Total Processing Time       : %,d ms (%.2f seconds)%n",
                totalProcessingTime.get(),
                totalProcessingTime.get() / 1000.0);
        System.out.println("=".repeat(60) + "\n");
    }
}