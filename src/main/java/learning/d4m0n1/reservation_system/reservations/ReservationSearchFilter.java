package learning.d4m0n1.reservation_system.reservations;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReservationSearchFilter(
         Long roomId,
         Long userId,
         @Min(1) @Max(100) Integer pageSize,
         @Min(0) Integer pageNumber
) {
    public ReservationSearchFilter {
        if (pageSize == null) pageSize = 10;
        if (pageNumber == null) pageNumber = 0;
    }
}
