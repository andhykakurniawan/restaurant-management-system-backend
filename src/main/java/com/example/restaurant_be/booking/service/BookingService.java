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
import com.example.restaurant_be.common.exception.ConflictException;
import com.example.restaurant_be.common.exception.NotFoundException;
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
                LocalDate bookingDate = LocalDate.parse(request.bookingDate());
                LocalTime bookingTime = LocalTime.parse(request.bookingTime());

                if (LocalDateTime.of(bookingDate, bookingTime).isBefore(LocalDateTime.now())) {
                        throw new ConflictException("Booking time must be in the future");
                }

                TableRestaurant table = tableRepository
                                .findByIdAndAllocationType(
                                                request.tableId(),
                                                AllocationType.ONLINE)
                                .orElseThrow(() -> new NotFoundException(
                                                "Table not available"));

                if (table.getStatus() == TableStatus.OCCUPIED) {
                        throw new ConflictException("Table is already occupied");
                } else if (table.getStatus() == TableStatus.RESERVED) {
                        throw new ConflictException("Table is already reserved");
                } else if (table.getStatus() == TableStatus.BLOCKED) {
                        throw new ConflictException("Table is under maintenance");
                }

                if (request.numberPerson() > table.getCapacity()) {
                        throw new ConflictException("Number of persons exceeds table capacity");
                }

                boolean hasBookingAtSameTime = bookingRepository
                                .existsByTable_IdAndBookingDateAndBookingTimeAndStatusIn(
                                                table.getId(),
                                                bookingDate,
                                                bookingTime,
                                                List.of(
                                                                BookingStatus.WAITING_PAYMENT,
                                                                BookingStatus.PENDING,
                                                                BookingStatus.CONFIRMED,
                                                                BookingStatus.CHECKED_IN));

                if (hasBookingAtSameTime) {
                        throw new ConflictException("Table already has a booking at this time");
                }

                Booking booking = new Booking();
                booking.setCustomerName(request.customerName());
                booking.setCustomerPhone(request.customerPhone());
                booking.setNumberPerson(request.numberPerson());
                booking.setTable(table);
                booking.setBookingDate(bookingDate);
                booking.setBookingTime(bookingTime);
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

                return toResponse(savedBooking);
        }

        @Transactional
        public BookingResponse cancelBooking(UUID id) {

                Booking booking = bookingRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Booking not found"));

                TableRestaurant table = booking.getTable();

                if (booking.getStatus() == BookingStatus.CONFIRMED ||
                                booking.getStatus() == BookingStatus.CHECKED_IN) {
                        throw new ConflictException("Booking cannot be cancelled at this stage");
                }

                if (booking.getStatus() == BookingStatus.CANCELLED) {
                        throw new ConflictException("Booking is already cancelled");
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
                                .orElseThrow(() -> new NotFoundException("Booking not found"));
                return toResponse(booking);
        }
}
