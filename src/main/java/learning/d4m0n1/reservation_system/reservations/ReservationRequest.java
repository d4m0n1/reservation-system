package learning.d4m0n1.reservation_system.reservations;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequest(
        @NotNull
        Long userId,
        @NotNull
        Long roomId,
        @FutureOrPresent
        @NotNull
        LocalDate startDate,
        @FutureOrPresent
        @NotNull
        LocalDate endDate
) {
        @AssertTrue(message = "endDate must be after startDate")
        public boolean isEndDateAfterStartDate() {
                return startDate == null || endDate == null || endDate.isAfter(startDate);
        }
}
