package scenario1;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe statistics tracking class
 */
public class SubmissionStats {

    // Match UML naming: successful and failed (not successfulSubmissions)
    private final AtomicInteger successful;
    private final AtomicInteger failed;

    public SubmissionStats() {
        this.successful = new AtomicInteger(0);
        this.failed = new AtomicInteger(0);
    }

    /**
     * Record a successful submission
     * Matches UML: recordSuccess()
     */
    public void recordSuccess() {
        successful.incrementAndGet();
    }

    /**
     * Record a failed submission
     * Matches UML: recordFailure()
     */
    public void recordFailure() {
        failed.incrementAndGet();
    }

    /**
     * Get total submissions
     */
    public int getTotalSubmissions() {
        return successful.get() + failed.get();
    }

    /**
     * Get successful submissions count
     */
    public int getSuccessful() {
        return successful.get();
    }

    /**
     * Get failed submissions count
     */
    public int getFailed() {
        return failed.get();
    }

    /**
     * Calculate success rate
     */
    public double getSuccessRate() {
        int total = getTotalSubmissions();
        if (total == 0) return 0.0;
        return (successful.get() * 100.0) / total;
    }

    /**
     * Print results with method name and total time
     * Matches UML: printResults(String method, long totalTimeMs)
     */
    public void printResults(String method, long totalTimeMs) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("     SUBMISSION SYSTEM STATISTICS - " + method);
        System.out.println("=".repeat(60));
        System.out.printf("Total Students Processed    : %,d%n", getTotalSubmissions());
        System.out.printf("Successful Submissions      : %,d%n", getSuccessful());
        System.out.printf("Failed Submissions          : %,d%n", getFailed());
        System.out.printf("Success Rate                : %.2f%%%n", getSuccessRate());
        System.out.printf("Total Processing Time       : %,d ms (%.2f seconds)%n",
                totalTimeMs, totalTimeMs / 1000.0);
        System.out.println("=".repeat(60) + "\n");
    }
}