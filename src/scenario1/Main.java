package scenario1;

import java.util.Scanner;

/**
 * Main class to run the submission system
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   EASTMINSTER UNIVERSITY SUBMISSION SYSTEM             ║");
        System.out.println("║   Concurrent Exam Submission Handler                   ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Test with different loads
        System.out.println("Select test scenario:");
        System.out.println("1. Small load (1,000 students)");
        System.out.println("2. Medium load (5,000 students)");
        System.out.println("3. Large load (10,000 students)");
        System.out.println("4. Massive load (50,000 students)");
        System.out.println("5. Extreme load (100,000 students)");
        System.out.println("6. Custom number");
        System.out.print("\nEnter choice (1-6): ");

        int choice = scanner.nextInt();
        int numberOfStudents = 0;

        switch (choice) {
            case 1: numberOfStudents = 1000; break;
            case 2: numberOfStudents = 5000; break;
            case 3: numberOfStudents = 10000; break;
            case 4: numberOfStudents = 50000; break;
            case 5: numberOfStudents = 100000; break;
            case 6:
                System.out.print("Enter number of students: ");
                numberOfStudents = scanner.nextInt();
                break;
            default:
                System.out.println("Invalid choice. Using 5,000 students.");
                numberOfStudents = 5000;
        }

        // Create and run the system
        NewSubmissionSystem system = new NewSubmissionSystem();

        try {
            system.processSubmissions(numberOfStudents);
            system.displayResults();
        } finally {
            system.shutdown();
        }

        scanner.close();
        System.out.println("System shutdown complete.");
    }
}