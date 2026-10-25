package learning.d4m0n1.reservation_system.reservations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDate;

public record Reservation(
        @Null
        Long id,
        @NotNull
        Long userId,
        @NotNull
        Long roomId,
        @FutureOrPresent
        @NotNull
        LocalDate startDate,
        @FutureOrPresent
        @NotNull
        LocalDate endDate,
        ReservationStatus status
) {
        @AssertTrue(message = "endDate must be after startDate")
        @JsonIgnore
        public boolean isEndDateAfterStartDate() {
                return startDate == null || endDate == null || endDate.isAfter(startDate);
        }
}
