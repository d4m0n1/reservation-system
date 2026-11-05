package learning.d4m0n1.reservation_system.reservations;

import jakarta.validation.Valid;
import learning.d4m0n1.reservation_system.web.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
    ) {
        log.info("Called getReservationById: id={}", id);
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<Reservation>> getAllReservations(
            @Valid ReservationSearchFilter filter
    ) {
        log.info("Called getAllReservations");
        return ResponseEntity.ok(PageResponse.from(reservationService.searchAllByFilter(filter)));
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody @Valid ReservationRequest reservationToCreate
    ) {
        log.info("Called createReservation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(reservationToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable("id") Long id,
            @RequestBody @Valid ReservationRequest reservationToUpdate
    ) {
        log.info("Called method updateReservation: id={}, " +
                "reservationToUpdate={}", id, reservationToUpdate);
        var updated = reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id
    ) {
        log.info("Called method cancelReservation: id={}", id);
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(
            @PathVariable Long id
    ) {
        log.info("Called method approveReservation: id={}", id);
        return ResponseEntity.ok(reservationService.approveReservation(id));
    }

}
