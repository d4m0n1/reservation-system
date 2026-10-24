package learning.d4m0n1.reservation_system.reservations.availability;

import learning.d4m0n1.reservation_system.reservations.ReservationRepository;
import learning.d4m0n1.reservation_system.reservations.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationAvailabilityService {

    private static final Logger log = LoggerFactory.getLogger(ReservationAvailabilityService.class);

    private final ReservationRepository repository;


    public ReservationAvailabilityService(ReservationRepository repository) {
        this.repository = repository;
    }


    public boolean isReservationAvailable(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than end date");
        }

        List<Long> conflictsIds = repository.findConflictReservationsIds(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );
        if (conflictsIds.isEmpty()) {
            return true;
        }
        log.info("Conflicting with ids = {}", conflictsIds);
        return false;
    }


}
