package learning.d4m0n1.reservation_system.reservations;

public record ReservationSearchFilter(
         Long roomId,
         Long userId,
         Integer pageSize,
         Integer pageNumber
) {

}
