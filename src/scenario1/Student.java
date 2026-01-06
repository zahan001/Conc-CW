package scenario1;

import java.util.Random;

/**
 * Represents a student submitting an exam
 * Matches UML specification from coursework
 */
public class Student {
    private int studentId;
    private String name;
    private Random random;

    /**
     * Constructor matching UML specification
     */
    public Student(int studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.random = new Random();
    }

    /**
     * Get student name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get student ID
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * Submit exam - simulates submission process
     * Returns boolean indicating success/failure
     *
     * CONCURRENCY: Each Student object accessed by single thread only
     * Random instance per student eliminates contention
     *
     * @return true if submission successful, false if failed
     * @throws InterruptedException if interrupted during submission
     */
    public boolean submitExam() throws InterruptedException {
        // Simulate variable processing time (0-100ms)
        int simulateTime = random.nextInt(100);
        Thread.sleep(simulateTime);

        // 5% failure rate
        int randomNumber = random.nextInt(100);
        return randomNumber >= 5;  // Returns false if < 5 (5% chance)
    }
}