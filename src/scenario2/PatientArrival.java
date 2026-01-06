package scenario2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Producer thread that continuously generates patients
 * Runs 24/7 creating patients at random intervals
 */
public class PatientArrival implements Runnable {

    private final Map<Specialty, PatientQueue> queues;
    private volatile boolean running;
    private int patientIdCounter;
    private final Random random;

    // Configuration
    private static final int MIN_ARRIVAL_INTERVAL_MS = 300;
    private static final int MAX_ARRIVAL_INTERVAL_MS = 700;

    /**
     * Constructor
     * @param queues Map of specialty to patient queue
     */
    public PatientArrival(Map<Specialty, PatientQueue> queues) {
        this.queues = queues;
        this.running = true;
        this.patientIdCounter = 1;
        this.random = new Random();
    }

    @Override
    public void run() {
        System.out.println("🚑 Patient arrival system started\n");

        try {
            while (running) {
                // Generate random patient
                Specialty specialty = Specialty.random();
                Patient patient = new Patient(patientIdCounter++, specialty);

                // Add to appropriate queue
                PatientQueue queue = queues.get(specialty);
                queue.addPatient(patient);

                System.out.printf("🚑 NEW: %s | Queue size: %d%n",
                        patient,
                        queue.getSize());

                // Random interval before next patient
                int interval = MIN_ARRIVAL_INTERVAL_MS +
                        random.nextInt(MAX_ARRIVAL_INTERVAL_MS - MIN_ARRIVAL_INTERVAL_MS);
                Thread.sleep(interval);
            }
        } catch (InterruptedException e) {
            System.out.println("🚑 Patient arrival system stopped");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stop the patient arrival system
     */
    public void stop() {
        running = false;
    }
}