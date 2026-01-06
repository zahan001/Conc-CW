package scenario2;

import java.util.HashMap;
import java.util.Map;

/**
 * Main simulation controller for hospital patient management
 * Coordinates producer (PatientArrival) and consumers (Consultants via ShiftManager)
 */
public class HospitalSimulation {

    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   ROYAL MANCHESTER HOSPITAL - PATIENT MANAGEMENT       ║");
        System.out.println("║   Producer-Consumer Concurrent System                  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        System.out.println("Time Scale: 1 simulated hour = 1 real second");
        System.out.println("Shift Duration: 12 simulated hours = 12 real seconds\n");

        // Create three separate queues (one per specialty)
        Map<Specialty, PatientQueue> queues = new HashMap<>();
        for (Specialty specialty : Specialty.values()) {
            queues.put(specialty, new PatientQueue(specialty));
        }

        System.out.println("✓ Created 3 specialty-specific patient queues");
        System.out.println("  - " + Specialty.PAEDIATRICIAN.getDisplayName() + " queue");
        System.out.println("  - " + Specialty.SURGEON.getDisplayName() + " queue");
        System.out.println("  - " + Specialty.CARDIOLOGIST.getDisplayName() + " queue\n");

        // Create and start patient arrival producer thread
        PatientArrival patientArrival = new PatientArrival(queues);
        Thread arrivalThread = new Thread(patientArrival);
        arrivalThread.start();

        // Create shift manager
        ShiftManager shiftManager = new ShiftManager(queues);

        // Run both shifts
        shiftManager.runShifts();

        // Stop patient arrivals
        System.out.println("=".repeat(60));
        System.out.println("  SYSTEM SHUTDOWN");
        System.out.println("=".repeat(60));

        patientArrival.stop();
        arrivalThread.interrupt();

        try {
            arrivalThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Final statistics
        System.out.println("\n✓ All systems stopped");
        System.out.println("\nFinal Queue Status:");
        int totalWaiting = 0;
        for (Map.Entry<Specialty, PatientQueue> entry : queues.entrySet()) {
            int size = entry.getValue().getSize();
            totalWaiting += size;
            System.out.printf("  %s: %d patients waiting%n",
                    entry.getKey().getDisplayName(),
                    size);
        }
        System.out.println("  Total: " + totalWaiting + " patients waiting");

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Hospital System Simulation Complete                  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }
}