package com.example.restaurant_be.booking.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.restaurant_be.booking.entity.Booking;
import com.example.restaurant_be.booking.entity.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query("SELECT b FROM Booking b")
    List<Booking> findAllBookings();

    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Optional<Booking> findBookingById(UUID id);

    Optional<Booking> findByMidtransOrderId(String midtransOrderId);

    Optional<Booking> findByBookingCode(String bookingCode);

    boolean existsByTable_IdAndBookingDateAndBookingTimeAndStatusIn(
            UUID tableId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            List<BookingStatus> statuses);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.expiredAt < :now
            AND b.status IN :statuses
            """)
    List<Booking> findExpiredBookings(
            @Param("now") LocalDateTime now,
            @Param("statuses") List<BookingStatus> statuses);
}
