package learning.d4m0n1.reservation_system.reservations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Modifying
    @Query("""
        UPDATE ReservationEntity r
        SET r.status = :status
        WHERE r.id = :id
        """)
    void setStatus(
            @Param("id") Long id,
            @Param("status") ReservationStatus reservationStatus
    );

    @Query("""
        SELECT r.id FROM ReservationEntity r
                WHERE r.roomId = :roomId
                AND :startDate < r.endDate
                AND r.startDate < :endDate
                AND r.status = :status
        """)
    List<Long> findConflictReservationsIds(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ReservationStatus status
    );

    @Query("""
        SELECT r FROM ReservationEntity r
                WHERE (:roomId IS NULL OR r.roomId = :roomId)
                AND (:roomId IS NULL OR r.userId = :userId)
        """)
    List<ReservationEntity> searchAllByFilter(
            Long roomId,
            Long userId,
            Pageable pageable
    );

}
