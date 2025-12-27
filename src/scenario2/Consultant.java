package scenario2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Consultant thread that treats patients
 * Each consultant runs continuously, taking patients from queue
 */
public class Consultant implements Runnable {

    private final String consultantName;
    private final Specialty specialty;
    private final BlockingQueue<Patient> patientQueue;
    private volatile boolean working; // volatile: visible across threads
    private int patientsSeenCount;

    private static final int CONSULTATION_TIME_MS = 100; // Simulated consultation time

    public Consultant(String name, Specialty specialty, BlockingQueue<Patient> queue) {
        this.consultantName = name;
        this.specialty = specialty;
        this.patientQueue = queue;
        this.working = true;
        this.patientsSeenCount = 0;
    }

    @Override
    public void run() {
        System.out.println(">>> " + consultantName + " (" + specialty.getDisplayName() +
                ") started shift");

        try {
            while (working) {
                // Try to get a patient (wait up to 2 seconds)
                Patient patient = patientQueue.poll(2, TimeUnit.SECONDS);

                if (patient != null) {
                    // Check if patient matches our specialty
                    if (patient.getRequiredSpecialty() == specialty) {
                        treatPatient(patient);
                    } else {
                        // Wrong specialty - put back in queue
                        patientQueue.put(patient);
                    }
                }

                // Check if shift should end
                if (!working) {
                    break;
                }
            }

        } catch (InterruptedException e) {
            System.out.println("<<< " + consultantName + " interrupted");
            Thread.currentThread().interrupt();
        }

        System.out.println("<<< " + consultantName + " ended shift. Patients seen: " +
                patientsSeenCount);
    }

    /**
     * Treat a patient (simulate consultation)
     */
    private void treatPatient(Patient patient) throws InterruptedException {
        System.out.printf("    [%s] treating %s%n",
                consultantName,
                patient);

        // Simulate consultation time
        Thread.sleep(CONSULTATION_TIME_MS);

        patientsSeenCount++;

        System.out.printf("    [%s] completed treating Patient #%d ✓%n",
                consultantName,
                patient.getPatientId());
    }

    /**
     * Stop the consultant (end shift)
     */
    public void stopWorking() {
        working = false;
    }

    public int getPatientsSeenCount() {
        return patientsSeenCount;
    }

    public String getConsultantName() {
        return consultantName;
    }
}