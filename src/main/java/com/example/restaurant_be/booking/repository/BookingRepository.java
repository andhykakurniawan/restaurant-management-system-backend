package com.example.restaurant_be.booking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.restaurant_be.booking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    @Query(value = "SELECT * FROM Bookings", nativeQuery = true)
    List<Booking> findAllBookings();

    @Query(value = "SELECT * FROM Bookings WHERE id = :id", nativeQuery = true)
    Optional<Booking> findBookingById(UUID id);
    Optional<Booking> findBookingByTable(UUID tableId);

    Optional<Booking> findByMidtransOrderId(String midtransOrderId);

    @Query(value = "SELECT * FROM Bookings WHERE booking_code = :bookingCode", nativeQuery = true)
    Optional<Booking> findByBookingCode(String bookingCode);
}
