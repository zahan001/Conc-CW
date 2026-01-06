package scenario1;

import java.util.Random;

/**
 * Represents a student submitting an exam
 */
public class Student {

    private int studentId;
    private String name;
    private Random random;

    /**
     * Constructor as per UML diagram
     */
    public Student(int studentId) {
        this.studentId = studentId;
        this.name = "Student_" + studentId;
        this.random = new Random();
    }

    /**
     * Simulate exam submission
     * Returns String message indicating success or failure
     */
    public String submitExam() {
        try {
            // Simulate processing time (50-200ms)
            int processingTime = 50 + random.nextInt(150);
            Thread.sleep(processingTime);

            // 5% failure rate
            boolean success = random.nextDouble() > 0.05;

            if (success) {
                return "SUCCESS";
            } else {
                return "FAILED";
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "ERROR";
        }
    }

    /**
     * Get student name as per UML
     */
    public String getName() {
        return name;
    }

    public int getStudentId() {
        return studentId;
    }
}