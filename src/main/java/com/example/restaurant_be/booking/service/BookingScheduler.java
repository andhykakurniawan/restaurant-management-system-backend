package com.example.restaurant_be.booking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.restaurant_be.audit.service.AuditLogService;
import com.example.restaurant_be.booking.entity.Booking;
import com.example.restaurant_be.booking.entity.BookingStatus;
import com.example.restaurant_be.booking.repository.BookingRepository;
import com.example.restaurant_be.ordersession.repository.OrderSessionRepository;
import com.example.restaurant_be.table.entity.TableRestaurant;
import com.example.restaurant_be.table.entity.TableStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingRepository bookingRepository;
    private final OrderSessionRepository orderSessionRepository;
    private final AuditLogService auditLogService;
    private static final int NO_SHOW_GRACE_MINUTES = 15;

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

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void markNoShowBookings() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findConfirmedBookingsBefore(
                BookingStatus.CONFIRMED,
                now.toLocalDate(),
                now.toLocalTime());

        for (Booking booking : bookings) {
            LocalDateTime noShowThreshold = LocalDateTime.of(
                    booking.getBookingDate(),
                    booking.getBookingTime())
                    .plusMinutes(booking.getDurationMinutes())
                    .plusMinutes(NO_SHOW_GRACE_MINUTES);

            if (now.isBefore(noShowThreshold)) {
                continue;
            }

            booking.setStatus(BookingStatus.NO_SHOW);
            booking.setNoShowAt(now);
            auditLogService.log("BOOKING_NO_SHOW", "Booking", booking.getId(), booking.getBookingCode());

            TableRestaurant table = booking.getTable();
            if (!orderSessionRepository.existsByTableAndIsActiveTrue(table)) {
                table.setStatus(TableStatus.AVAILABLE);
            }

            bookingRepository.save(booking);
        }
    }
}
