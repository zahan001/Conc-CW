package scenario2;

/**
 * Consumer thread representing a consultant treating patients
 * Each consultant takes patients from ONE specialty queue only
 */
public class Consultant implements Runnable {

    private final String consultantName;
    private final Specialty specialty;
    private final PatientQueue patientQueue;
    private volatile boolean working;
    private int patientsSeenCount;

    private static final int CONSULTATION_TIME_MS = 100; // Simulated treatment time

    /**
     * Constructor
     * @param name Consultant name
     * @param specialty Consultant's specialty
     * @param queue The queue for this specialty ONLY
     */
    public Consultant(String name, Specialty specialty, PatientQueue queue) {
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
                // Take patient from queue (blocks if empty)
                Patient patient = patientQueue.takePatient();

                // Verify specialty matches (should always match with separate queues)
                if (patient.getRequiredSpecialty() == specialty) {
                    treatPatient(patient);
                } else {
                    // This should NEVER happen with separate queues
                    System.err.println("ERROR: " + consultantName +
                            " received wrong specialty patient!");
                }
            }
        } catch (InterruptedException e) {
            // Shift ended - interrupt received
            System.out.println("<<< " + consultantName + " ending shift...");
        }

        System.out.println("<<< " + consultantName + " ended shift. Patients seen: " +
                patientsSeenCount);
    }

    /**
     * Treat a patient (simulate consultation)
     */
    private void treatPatient(Patient patient) throws InterruptedException {
        System.out.printf("    [%s] treating %s%n", consultantName, patient);

        // Simulate consultation time
        Thread.sleep(CONSULTATION_TIME_MS);

        patientsSeenCount++;

        System.out.printf("    [%s] completed treating Patient #%d ✓%n",
                consultantName,
                patient.getPatientId());
    }

    /**
     * Stop working (called by ShiftManager)
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