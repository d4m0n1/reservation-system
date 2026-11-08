package learning.d4m0n1.reservation_system.reservations;

public class ReservationConflictException extends RuntimeException {

    public ReservationConflictException(String message) {
        super(message);
    }
}
