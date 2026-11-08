package learning.d4m0n1.reservation_system.reservations;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(Long id) {
        super("Not found reservation by id = " + id);
    }
}
