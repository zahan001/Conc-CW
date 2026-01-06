package scenario1;

import java.util.Scanner;

/**
 * ExamSubmissionSimulator
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   EASTMINSTER UNIVERSITY - EXAM SUBMISSION SYSTEM      ║");
        System.out.println("║   Concurrent Multi-Threaded Solution                   ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        System.out.println("This new system replaces the old sequential submission system");
        System.out.println("that caused 20-30 minute wait times and timeouts.\n");

        // Test scenarios
        System.out.println("Select test scenario:");
        System.out.println("1. Small load (1,000 students)");
        System.out.println("2. Medium load (5,000 students) - Original capacity");
        System.out.println("3. Large load (10,000 students)");
        System.out.println("4. Very large load (35,000 students) - Current enrollment");
        System.out.println("5. Massive load (50,000 students)");
        System.out.println("6. Extreme load (100,000 students) - 2030 projection");
        System.out.println("7. Custom number");
        System.out.print("\nEnter choice (1-7): ");

        int choice = scanner.nextInt();
        int numberOfStudents = 0;

        switch (choice) {
            case 1: numberOfStudents = 1000; break;
            case 2: numberOfStudents = 5000; break;
            case 3: numberOfStudents = 10000; break;
            case 4: numberOfStudents = 35000; break;
            case 5: numberOfStudents = 50000; break;
            case 6: numberOfStudents = 100000; break;
            case 7:
                System.out.print("Enter number of students: ");
                numberOfStudents = scanner.nextInt();
                break;
            default:
                System.out.println("Invalid choice. Using 5,000 students.");
                numberOfStudents = 5000;
        }

        // Calculate optimal thread pool size
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;

        // Create and run the new system
        NewSubmissionSystem system = new NewSubmissionSystem(numberOfStudents, poolSize);

        try {
            system.processSubmissions(numberOfStudents);
        } finally {
            system.shutdown();
        }

        scanner.close();

        /*System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   COMPARISON: Old vs New System                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("Old System (Sequential):");
        System.out.println("  - 5,000 students = 20-30 minutes");
        System.out.println("  - Single threaded, processed one at a time");
        System.out.println("  - Frequent timeouts and failures");
        System.out.println("\nNew System (Concurrent):");
        System.out.println("  - 5,000 students = ~20 seconds");
        System.out.println("  - " + poolSize + " threads working simultaneously");
        System.out.println("  - Scales to 100,000+ students");
        System.out.println("  - Graceful error handling\n");*/
    }
}