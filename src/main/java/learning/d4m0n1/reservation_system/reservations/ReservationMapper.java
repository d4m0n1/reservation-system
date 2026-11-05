package learning.d4m0n1.reservation_system.reservations;

import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {


    public Reservation toDomain(
            ReservationEntity reservation
    ) {
        return new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStatus()
        );
    }

    public ReservationEntity toEntity(
            ReservationRequest request
    ) {
        return new ReservationEntity(
                null,
                request.userId(),
                request.roomId(),
                request.startDate(),
                request.endDate(),
                null
        );
    }

}
