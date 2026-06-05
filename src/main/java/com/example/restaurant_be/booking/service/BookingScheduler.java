package com.example.restaurant_be.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.restaurant_be.booking.entity.Booking;
import com.example.restaurant_be.booking.entity.BookingStatus;
import com.example.restaurant_be.booking.repository.BookingRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingRepository bookingRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireUnpaidBookings() {
        List<Booking> bookings = bookingRepository.findExpiredBookings(
                LocalDateTime.now(),
                List.of(
                        BookingStatus.WAITING_PAYMENT,
                        BookingStatus.PENDING));

        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.EXPIRED);
            booking.setCancelledAt(LocalDateTime.now());
            bookingRepository.save(booking);
        }
    }
}
