package scenario2;

/**
 * Main class to run the hospital system
 */
public class Main {

    public static void main(String[] args) {
        HospitalSystem hospital = new HospitalSystem();
        hospital.start();

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Hospital System Simulation Complete                  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
    }
}