package learning.d4m0n1.reservation_system.reservations.availability;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CheckAvailabilityRequest(
        @NotNull
        Long roomId,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate
) {
        @AssertTrue(message = "endDate must be after startDate")
        public boolean isEndDateAfterStartDate() {
                return startDate == null || endDate == null || endDate.isAfter(startDate);
        }
}
