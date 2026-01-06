package scenario2;

import java.util.concurrent.*;
import java.util.*;

/**
 * Main hospital system managing consultants, patients, and shifts
 */
public class HospitalSystem {

    // Shared patient queue - thread-safe
    private final BlockingQueue<Patient> patientQueue;

    // Current shift consultants and their threads
    private List<Consultant> currentConsultants;
    private List<Thread> currentConsultantThreads;

    // Patient generation
    private Thread patientArrivalThread;
    private volatile boolean systemRunning;
    private int patientIdCounter;

    // Configuration
    private static final int CONSULTANTS_PER_SHIFT = 3;
    private static final int SHIFT_DURATION_MS = 12000; // 12 seconds = 12 hours simulated
    private static final int PATIENT_ARRIVAL_INTERVAL_MS = 500; // New patient every 0.5 sec

    public HospitalSystem() {
        // LinkedBlockingQueue: unbounded, thread-safe FIFO queue
        this.patientQueue = new LinkedBlockingQueue<>();
        this.currentConsultants = new ArrayList<>();
        this.currentConsultantThreads = new ArrayList<>();
        this.systemRunning = true;
        this.patientIdCounter = 1;

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   ROYAL MANCHESTER HOSPITAL - PATIENT MANAGEMENT       ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Start the hospital system
     */
    public void start() {
        // Start patient arrival thread
        startPatientArrivals();

        // Run two complete shifts (day and night)
        runShift("DAY SHIFT", 1);
        runShift("NIGHT SHIFT", 2);

        // Stop system
        stopSystem();
    }

    /**
     * Start continuous patient arrivals in separate thread
     */
    private void startPatientArrivals() {
        patientArrivalThread = new Thread(() -> {
            System.out.println("🚑 Patient arrival system started\n");

            try {
                while (systemRunning) {
                    // Generate new patient with random specialty
                    Patient patient = new Patient(
                            patientIdCounter++,
                            Specialty.random()
                    );

                    // Add to queue (thread-safe operation)
                    patientQueue.put(patient);
                    System.out.println("🚑 NEW: " + patient + " | Queue size: " + patientQueue.size());

                    // Wait before next patient
                    Thread.sleep(PATIENT_ARRIVAL_INTERVAL_MS);
                }
            } catch (InterruptedException e) {
                System.out.println("Patient arrival system stopped");
                Thread.currentThread().interrupt();
            }
        });

        patientArrivalThread.start();
    }

    /**
     * Run a single shift with 3 consultants
     */
    private void runShift(String shiftName, int shiftNumber) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + shiftName + " - Starting");
        System.out.println("=".repeat(60) + "\n");

        // Create consultants for this shift
        currentConsultants = new ArrayList<>();
        currentConsultantThreads = new ArrayList<>();

        // Create one consultant per specialty
        Specialty[] specialties = Specialty.values();
        for (int i = 0; i < CONSULTANTS_PER_SHIFT; i++) {
            Specialty specialty = specialties[i % specialties.length];
            String consultantName = "Dr. " + getConsultantName(shiftNumber, i + 1);

            Consultant consultant = new Consultant(consultantName, specialty, (PatientQueue) patientQueue);
            Thread consultantThread = new Thread(consultant);

            currentConsultants.add(consultant);
            currentConsultantThreads.add(consultantThread);

            consultantThread.start();
        }

        // Let shift run for specified duration
        try {
            System.out.println("⏰ Shift will run for " + (SHIFT_DURATION_MS / 1000) + " seconds\n");
            Thread.sleep(SHIFT_DURATION_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // End shift
        endShift(shiftName);
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

        // Wait for all consultant threads to finish
        for (Thread thread : currentConsultantThreads) {
            try {
                thread.join(3000); // Wait up to 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Display shift statistics
        System.out.println("\nShift Summary:");
        int totalPatientsSeen = 0;
        for (Consultant consultant : currentConsultants) {
            int count = consultant.getPatientsSeenCount();
            totalPatientsSeen += count;
            System.out.printf("  %s: %d patients%n",
                    consultant.getConsultantName(),
                    count);
        }
        System.out.println("  Total patients treated: " + totalPatientsSeen);
        System.out.println("  Patients still waiting: " + patientQueue.size());
        System.out.println();
    }

    /**
     * Stop the entire system
     */
    private void stopSystem() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  SYSTEM SHUTDOWN");
        System.out.println("=".repeat(60));

        systemRunning = false;

        // Stop patient arrivals
        if (patientArrivalThread != null) {
            patientArrivalThread.interrupt();
            try {
                patientArrivalThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("✓ All systems stopped");
        System.out.println("✓ Final queue size: " + patientQueue.size());
    }

    /**
     * Generate consultant name for demonstration
     */
    private String getConsultantName(int shift, int number) {
        String[][] names = {
                {"Smith", "Johnson", "Williams"},  // Shift 1
                {"Brown", "Davis", "Miller"}       // Shift 2
        };
        return names[shift - 1][number - 1];
    }
}