package learning.d4m0n1.reservation_system.reservations;

import jakarta.persistence.EntityNotFoundException;
import learning.d4m0n1.reservation_system.reservations.availability.ReservationAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private ReservationAvailabilityService availabilityService;

    private ReservationService service;

    @BeforeEach
    void setUp() {
        service = new ReservationService(repository, new ReservationMapper(), availabilityService);
    }

    @Test
    void createReservation_setsPendingStatus() {
        var request = new ReservationRequest(1L, 5L, LocalDate.now(), LocalDate.now().plusDays(3));
        when(repository.save(any())).thenAnswer(invocation -> {
            ReservationEntity entity = invocation.getArgument(0);
            entity.setId(10L);
            return entity;
        });

        var result = service.createReservation(request);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.status()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void approveReservation_whenRoomIsFree_setsApprovedStatus() {
        var entity = reservation(ReservationStatus.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(availabilityService.isReservationAvailable(any(), any(), any())).thenReturn(true);

        var result = service.approveReservation(1L);

        assertThat(result.status()).isEqualTo(ReservationStatus.APPROVED);
    }

    @Test
    void approveReservation_whenDatesConflict_throwsAndKeepsPending() {
        var entity = reservation(ReservationStatus.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(availabilityService.isReservationAvailable(any(), any(), any())).thenReturn(false);

        assertThatThrownBy(() -> service.approveReservation(1L))
                .isInstanceOf(IllegalStateException.class);
        assertThat(entity.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void approveReservation_whenNotPending_throws() {
        var entity = reservation(ReservationStatus.CANCELLED);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.approveReservation(1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getReservationById_whenNotFound_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReservationById(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void cancelReservation_whenPending_setsCancelledStatus() {
        var entity = reservation(ReservationStatus.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.cancelReservation(1L);

        assertThat(entity.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void cancelReservation_whenNotPending_throws() {
        var entity = reservation(ReservationStatus.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.cancelReservation(1L))
                .isInstanceOf(IllegalStateException.class);
        assertThat(entity.getStatus()).isEqualTo(ReservationStatus.APPROVED);
    }

    private ReservationEntity reservation(ReservationStatus status) {
        return new ReservationEntity(
                1L, 1L, 5L,
                LocalDate.now(), LocalDate.now().plusDays(3),
                status
        );
    }

}
