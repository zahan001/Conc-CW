package scenario2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a patient in the system
 */
public class Patient {

    private final int patientId;
    private final Specialty requiredSpecialty;
    private final LocalDateTime arrivalTime;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Patient(int patientId, Specialty requiredSpecialty) {
        this.patientId = patientId;
        this.requiredSpecialty = requiredSpecialty;
        this.arrivalTime = LocalDateTime.now();
    }

    public int getPatientId() {
        return patientId;
    }

    public Specialty getRequiredSpecialty() {
        return requiredSpecialty;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public String getFormattedArrivalTime() {
        return arrivalTime.format(TIME_FORMATTER);
    }

    @Override
    public String toString() {
        return String.format("Patient #%d (%s) - Arrived: %s",
                patientId,
                requiredSpecialty.getDisplayName(),
                getFormattedArrivalTime());
    }
}