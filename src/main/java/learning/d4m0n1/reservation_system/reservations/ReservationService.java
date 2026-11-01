package learning.d4m0n1.reservation_system.reservations;

import jakarta.persistence.EntityNotFoundException;
import learning.d4m0n1.reservation_system.reservations.availability.ReservationAvailabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository repository;

    private final ReservationMapper mapper;

    private final ReservationAvailabilityService availabilityService;

    public ReservationService(ReservationRepository repository, ReservationMapper mapper, ReservationAvailabilityService availabilityService) {
        this.repository = repository;
        this.mapper = mapper;
        this.availabilityService = availabilityService;
    }

    @Transactional(readOnly = true)
    public Reservation getReservationById(
            Long id
    ) {
        var reservationEntity = findReservation(id);
        return mapper.toDomain(reservationEntity);
    }

    @Transactional(readOnly = true)
    public List<Reservation> searchAllByFilter(
            ReservationSearchFilter filter
    ) {
        var pageable = Pageable
                .ofSize(filter.pageSize())
                .withPage(filter.pageNumber());

        List<ReservationEntity> allEntities = repository.searchAllByFilter(
                filter.roomId(),
                filter.userId(),
                pageable
        );

        return allEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Transactional
    public Reservation createReservation(Reservation reservationToCreate) {
        var entityToSave = mapper.toEntity(reservationToCreate);
        entityToSave.setStatus(ReservationStatus.PENDING);

        var savedEntity = repository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Transactional
    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        var reservationEntity = findReservation(id);

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation status="
                    + reservationEntity.getStatus());
        }

        reservationEntity.setUserId(reservationToUpdate.userId());
        reservationEntity.setRoomId(reservationToUpdate.roomId());
        reservationEntity.setStartDate(reservationToUpdate.startDate());
        reservationEntity.setEndDate(reservationToUpdate.endDate());

        return mapper.toDomain(reservationEntity);
    }

    @Transactional
    public void cancelReservation(Long id) {
        var reservation = findReservation(id);
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel reservation status="
                    + reservation.getStatus());
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        log.info("Successfully cancelled reservation: id={}", id);
    }

    @Transactional
    public Reservation approveReservation(Long id) {
        var reservationEntity = findReservation(id);

        repository.lockAllByRoomId(reservationEntity.getRoomId()); // Блокировка

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot approve reservation status="
                    + reservationEntity.getStatus());
        }

        var isAvailableToApprove = availabilityService.isReservationAvailable(
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate()
        );

        if (!isAvailableToApprove) {
            throw new IllegalStateException("Cannot approve reservation because of conflict");
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);

        return mapper.toDomain(reservationEntity);
    }

    private ReservationEntity findReservation(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation by id = " + id
                ));
    }



}
