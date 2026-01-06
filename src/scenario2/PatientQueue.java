package scenario2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread-safe queue for patients of one specialty
 * Shared resource between producer (PatientArrival) and consumers (Consultants)
 *
 * NOTE: This is NOT a generic class - it specifically holds Patient objects
 */
public class PatientQueue {

    private final BlockingQueue<Patient> queue;
    private final Specialty specialty;

    /**
     * Constructor
     * @param specialty The medical specialty this queue serves
     */
    public PatientQueue(Specialty specialty) {
        this.queue = new LinkedBlockingQueue<>();
        this.specialty = specialty;
    }

    /**
     * Add patient to queue (called by producer)
     * Thread-safe operation
     * @param patient The patient to add
     * @throws InterruptedException if interrupted while waiting
     */
    public void addPatient(Patient patient) throws InterruptedException {
        queue.put(patient);
    }

    /**
     * Take patient from queue (called by consumer)
     * Blocks if queue is empty - thread-safe
     * @return The next patient in the queue
     * @throws InterruptedException if interrupted while waiting
     */
    public Patient takePatient() throws InterruptedException {
        return queue.take();
    }

    /**
     * Get current queue size
     * @return Number of patients waiting in this queue
     */
    public int getSize() {
        return queue.size();
    }

    /**
     * Get the specialty this queue serves
     * @return The specialty
     */
    public Specialty getSpecialty() {
        return specialty;
    }
}