package scenario2;

/**
 * Enum representing medical specialties
 */
public enum Specialty {
    PAEDIATRICIAN("Paediatrician"),
    SURGEON("Surgeon"),
    CARDIOLOGIST("Cardiologist");

    private final String displayName;

    Specialty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get a random specialty (for patient generation)
     */
    public static Specialty random() {
        Specialty[] specialties = values();
        return specialties[(int) (Math.random() * specialties.length)];
    }
}