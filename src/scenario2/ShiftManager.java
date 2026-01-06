package scenario2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages consultant shifts (day and night)
 * Controls starting/stopping consultant threads
 * Ensures queues persist across shifts
 */
public class ShiftManager {

    private final Map<Specialty, PatientQueue> queues;
    private List<Consultant> currentConsultants;
    private List<Thread> currentConsultantThreads;

    // Time scale: 1 simulated hour = 1 real second
    // 12-hour shift = 12 seconds
    private static final int SHIFT_DURATION_MS = 12000; // 12 seconds

    public ShiftManager(Map<Specialty, PatientQueue> queues) {
        this.queues = queues;
        this.currentConsultants = new ArrayList<>();
        this.currentConsultantThreads = new ArrayList<>();
    }

    /**
     * Run both shifts (day and night)
     */
    public void runShifts() {
        // Run day shift
        runShift("DAY SHIFT", getDayShiftConsultants());

        // Run night shift
        runShift("NIGHT SHIFT", getNightShiftConsultants());
    }

    /**
     * Run a single shift
     */
    private void runShift(String shiftName, String[][] consultantNames) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + shiftName + " - Starting");
        System.out.println("=".repeat(60));
        System.out.println("⏰ Shift will run for " + (SHIFT_DURATION_MS / 1000) + " seconds\n");

        // Start consultants
        startConsultants(consultantNames);

        // Let shift run for duration
        try {
            Thread.sleep(SHIFT_DURATION_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // End shift
        endShift(shiftName);
    }

    /**
     * Start consultant threads for this shift
     */
    private void startConsultants(String[][] consultantNames) {
        currentConsultants = new ArrayList<>();
        currentConsultantThreads = new ArrayList<>();

        Specialty[] specialties = Specialty.values();

        for (int i = 0; i < specialties.length; i++) {
            Specialty specialty = specialties[i];
            String name = consultantNames[i][0];

            // Get the queue for this specialty
            PatientQueue queue = queues.get(specialty);

            // Create consultant
            Consultant consultant = new Consultant(name, specialty, queue);
            Thread thread = new Thread(consultant);

            currentConsultants.add(consultant);
            currentConsultantThreads.add(thread);

            thread.start();
        }
    }

    /**
     * End current shift gracefully
     */
    private void endShift(String shiftName) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + shiftName + " - Ending");
        System.out.println("=".repeat(60));

        // Signal all consultants to stop
        for (Consultant consultant : currentConsultants) {
            consultant.stopWorking();
        }

        // Interrupt threads (to break blocking take())
        for (Thread thread : currentConsultantThreads) {
            thread.interrupt();
        }

        // Wait for threads to finish
        for (Thread thread : currentConsultantThreads) {
            try {
                thread.join(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Display statistics
        displayShiftSummary();
    }

    /**
     * Display shift statistics
     */
    private void displayShiftSummary() {
        System.out.println("\nShift Summary:");
        int totalPatients = 0;

        for (Consultant consultant : currentConsultants) {
            int count = consultant.getPatientsSeenCount();
            totalPatients += count;
            System.out.printf("  %s: %d patients%n",
                    consultant.getConsultantName(),
                    count);
        }

        System.out.println("  Total patients treated: " + totalPatients);

        // Show queue sizes (patients waiting per specialty)
        System.out.println("\nPatients still waiting by specialty:");
        for (Map.Entry<Specialty, PatientQueue> entry : queues.entrySet()) {
            System.out.printf("  %s: %d patients%n",
                    entry.getKey().getDisplayName(),
                    entry.getValue().getSize());
        }
        System.out.println();
    }

    /**
     * Get day shift consultant names
     */
    private String[][] getDayShiftConsultants() {
        return new String[][] {
                {"Dr. Smith"},      // Paediatrician
                {"Dr. Johnson"},    // Surgeon
                {"Dr. Williams"}    // Cardiologist
        };
    }

    /**
     * Get night shift consultant names
     */
    private String[][] getNightShiftConsultants() {
        return new String[][] {
                {"Dr. Brown"},      // Paediatrician
                {"Dr. Davis"},      // Surgeon
                {"Dr. Miller"}      // Cardiologist
        };
    }
}