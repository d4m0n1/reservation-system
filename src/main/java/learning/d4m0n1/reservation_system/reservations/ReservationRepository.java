package learning.d4m0n1.reservation_system.reservations;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

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
                AND (:userId IS NULL OR r.userId = :userId)
        """)
    Page<ReservationEntity> searchAllByFilter(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId,
            Pageable pageable
    );

    // Для блокировки других записей, пока текущая не закоммитится
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservationEntity r WHERE r.roomId = :roomId")
    List<ReservationEntity> lockAllByRoomId(@Param("roomId") Long roomId);
    // Spring Data требует соответствующее возвращаемое значение, поэтому List есть, хоть и не используется

}
