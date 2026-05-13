package com.example.restaurant_be.booking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.booking.dto.BookingRequest;
import com.example.restaurant_be.booking.dto.BookingResponse;
import com.example.restaurant_be.booking.entity.Booking;
import com.example.restaurant_be.booking.entity.BookingStatus;
import com.example.restaurant_be.booking.repository.BookingRepository;
import com.example.restaurant_be.payment.service.MidtransService;
import com.example.restaurant_be.table.entity.AllocationType;
import com.example.restaurant_be.table.entity.TableRestaurant;
import com.example.restaurant_be.table.entity.TableStatus;
import com.example.restaurant_be.table.repository.TableRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
        private final BookingRepository bookingRepository;
        private final MidtransService midtransService;
        private final TableRepository tableRepository;

        private BookingResponse toResponse(Booking booking) {
                return new BookingResponse(
                                booking.getId(),
                                booking.getBookingCode(),
                                booking.getCustomerName(),
                                booking.getCustomerPhone(),
                                booking.getNumberPerson(),
                                booking.getTable().getId(),
                                booking.getTable().getTableNumber(),
                                booking.getBookingDate(),
                                booking.getBookingTime(),
                                booking.getDpAmount(),
                                booking.getSnapToken(),
                                booking.getStatus().name());
        }

        public List<BookingResponse> getAllBookings() {
                List<Booking> bookings = bookingRepository.findAllBookings();
                return bookings.stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());
        }

        public BookingResponse create(BookingRequest request) {

                TableRestaurant table = tableRepository
                                .findByIdAndAllocationType(
                                                request.tableId(),
                                                AllocationType.ONLINE)
                                .orElseThrow(() -> new RuntimeException(
                                                "Table not available"));

                if (table.getStatus() == TableStatus.OCCUPIED) {
                        throw new IllegalStateException("Table is already occupied");
                } else if (table.getStatus() == TableStatus.RESERVED) {
                        throw new IllegalStateException("Table is already reserved");
                } else if (table.getStatus() == TableStatus.BLOCKED) {
                        throw new IllegalStateException("Table is under maintenance");
                }

                Booking booking = new Booking();
                booking.setCustomerName(request.customerName());
                booking.setCustomerPhone(request.customerPhone());
                booking.setNumberPerson(request.numberPerson());
                booking.setTable(table);
                booking.setBookingDate(LocalDate.parse(request.bookingDate()));
                booking.setBookingTime(LocalTime.parse(request.bookingTime()));
                booking.setDpAmount(request.dpAmount());
                booking.setStatus(BookingStatus.WAITING_PAYMENT);
                booking.setHasPreorder(false);

                String BookingCode = "BOOK-" + UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();
                booking.setBookingCode(BookingCode);

                String midtransOrderId = "ORDER-" + UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();
                booking.setMidtransOrderId(midtransOrderId);

                String snapToken = midtransService.createSnapToken(
                                midtransOrderId,
                                request.dpAmount()
                                                .doubleValue(),
                                request.customerName(),
                                request.customerPhone());

                booking.setSnapToken(
                                snapToken);

                booking.setExpiredAt(
                                LocalDateTime.now()
                                                .plusMinutes(15));

                Booking savedBooking = bookingRepository.save(booking);

                table.setStatus(TableStatus.OCCUPIED);
                tableRepository.save(table);

                return toResponse(savedBooking);
        }

        @Transactional
        public BookingResponse cancelBooking(UUID id) {

                Booking booking = bookingRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                TableRestaurant table = booking.getTable();

                if (booking.getStatus() == BookingStatus.CONFIRMED ||
                                booking.getStatus() == BookingStatus.CHECKED_IN) {
                        throw new IllegalStateException("Booking cannot be cancelled at this stage");
                }

                if (booking.getStatus() == BookingStatus.CANCELLED) {
                        throw new IllegalStateException("Booking is already cancelled");
                }

                booking.setStatus(BookingStatus.CANCELLED);
                booking.setCancelledAt(LocalDateTime.now());

                Booking updatedBooking = bookingRepository.save(booking);

                table.setStatus(TableStatus.AVAILABLE);
                tableRepository.save(table);

                return toResponse(updatedBooking);
        }

        public BookingResponse getBookingByCode(String bookingCode) {
                Booking booking = bookingRepository.findByBookingCode(bookingCode)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));
                return toResponse(booking);
        }
}
