package learning.d4m0n1.reservation_system.reservations;

import java.time.LocalDate;

public record Reservation(
        Long id,
        Long userId,
        Long roomId,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status
) {
}
