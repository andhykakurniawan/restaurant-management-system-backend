package com.example.restaurant_be.ordersession.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.restaurant_be.booking.entity.Booking;
import com.example.restaurant_be.booking.entity.BookingStatus;
import com.example.restaurant_be.booking.repository.BookingRepository;
import com.example.restaurant_be.common.exception.BadRequestException;
import com.example.restaurant_be.common.exception.ConflictException;
import com.example.restaurant_be.common.exception.NotFoundException;
import com.example.restaurant_be.ordersession.dto.OrderSessionRequest;
import com.example.restaurant_be.ordersession.dto.OrderSessionResponse;
import com.example.restaurant_be.ordersession.entity.OrderSession;
import com.example.restaurant_be.ordersession.entity.SessionStatus;
import com.example.restaurant_be.ordersession.repository.OrderSessionRepository;
import com.example.restaurant_be.order.entity.Order;
import com.example.restaurant_be.order.entity.Status;
import com.example.restaurant_be.order.repository.OrderRepository;
import com.example.restaurant_be.table.entity.TableRestaurant;
import com.example.restaurant_be.table.entity.TableStatus;
import com.example.restaurant_be.table.repository.TableRepository;
import com.example.restaurant_be.user.entity.User;
import com.example.restaurant_be.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderSessionService {

        private final OrderSessionRepository orderSessionRepository;

        private final TableRepository tableRepository;

        private final UserRepository userRepository;

        private final QrCodeService qrCodeService;

        private final BookingRepository bookingRepository;

        private final OrderRepository orderRepository;

        @Value("${app.frontend-url}")
        private String frontendUrl;

        public List<OrderSessionResponse> findAll() {
                List<OrderSession> sessions = orderSessionRepository.findAllIncludingInactive();
                return sessions.stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Transactional
        public OrderSessionResponse createSession(OrderSessionRequest request) {
                if (request.bookingId() != null) {
                        return checkInBooking(request);
                }

                return createWalkInSession(request);
        }

        @Transactional
        public OrderSessionResponse createWalkInSession(OrderSessionRequest request) {
                User user = getAuthenticatedUser();

                TableRestaurant table = tableRepository.findByIdIncludingInactive(request.tableId())
                                .orElseThrow(() -> new NotFoundException("Table not found"));

                validateTableHasNoActiveSession(table);

                if (table.getStatus() != TableStatus.AVAILABLE) {
                        throw new ConflictException("Walk-in session requires an available table");
                }

                OrderSession saved = openSession(table, user, null);

                return toResponse(saved);
        }

        @Transactional
        public OrderSessionResponse checkInBooking(OrderSessionRequest request) {
                User user = getAuthenticatedUser();

                if (request.bookingId() == null) {
                        throw new BadRequestException("Booking id is required");
                }

                Booking booking = bookingRepository.findById(request.bookingId())
                                .orElseThrow(() -> new NotFoundException("Booking not found"));

                TableRestaurant table = tableRepository.findByIdIncludingInactive(request.tableId())
                                .orElseThrow(() -> new NotFoundException("Table not found"));

                if (!booking.getTable().getId().equals(table.getId())) {
                        throw new ConflictException("Booking does not belong to this table");
                }

                validateTableHasNoActiveSession(table);

                if (booking.getStatus() != BookingStatus.CONFIRMED) {
                        throw new ConflictException("Booking is not confirmed");
                }

                if (table.getStatus() != TableStatus.RESERVED) {
                        throw new ConflictException("Table is not reserved");
                }

                booking.setStatus(BookingStatus.CHECKED_IN);
                bookingRepository.save(booking);

                OrderSession saved = openSession(table, user, booking);

                return toResponse(saved);
        }

        private User getAuthenticatedUser() {
                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                String email = authentication.getName();

                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("User tidak ditemukan"));
        }

        private void validateTableHasNoActiveSession(TableRestaurant table) {
                boolean hasActiveSession = orderSessionRepository.existsByTableAndIsActiveTrue(table);

                if (hasActiveSession) {
                        throw new ConflictException("Table still has active session");
                }
        }

        private OrderSession openSession(TableRestaurant table, User user, Booking booking) {
                String sessionToken = UUID.randomUUID().toString();

                LocalDateTime startedAt = LocalDateTime.now();

                LocalDateTime expiredAt = startedAt.plusMinutes(15);

                OrderSession session = new OrderSession();
                session.setTable(table);
                session.setCreatedBy(user);
                session.setBooking(booking);
                session.setSessionToken(sessionToken);
                session.setIsActive(true);
                session.setStartedAt(startedAt);
                session.setStatus(SessionStatus.WAITING_ORDER);
                session.setExpiredAt(expiredAt);
                session.setClosedAt(null);

                OrderSession saved = orderSessionRepository.save(session);

                table.setStatus(TableStatus.OCCUPIED);
                tableRepository.save(table);

                return saved;
        }

        private OrderSessionResponse toResponse(OrderSession session) {
                return new OrderSessionResponse(
                                session.getId(),
                                session.getTable().getId(),
                                session.getTable().getTableNumber(),
                                session.getCreatedBy().getId(),
                                session.getCreatedBy().getUsername(),
                                session.getBooking() != null ? session.getBooking().getId() : null,
                                session.getBooking() != null ? session.getBooking().getBookingCode() : null,
                                session.getSessionToken(),
                                frontendUrl + "/order?token=" + session.getSessionToken(),
                                qrCodeService.generateBase64QrCode(
                                                frontendUrl + "/order?token=" + session.getSessionToken(),
                                                300,
                                                300),
                                session.getStatus(),
                                session.getClosedAt() == null,
                                session.getStartedAt().toString(),
                                session.getClosedAt() != null ? session.getClosedAt().toString() : null);
        }

        @Transactional
        public void closeSession(UUID Id) {

                OrderSession session = orderSessionRepository
                                .findById(Id)
                                .orElseThrow(() -> new NotFoundException(
                                                "Session not found"));

                orderRepository.findByOrderSession_Id(session.getId())
                                .ifPresent(this::validateOrderCanCloseSession);

                session.setStatus(
                                SessionStatus.CLOSED);

                session.setClosedAt(
                                LocalDateTime.now());

                session.setIsActive(false);

                session.getTable()
                                .setStatus(
                                                TableStatus.AVAILABLE);

                tableRepository.save(
                                session.getTable());

                orderSessionRepository.save(session);
        }

        private void validateOrderCanCloseSession(Order order) {
                if (order.getStatus() != Status.COMPLETED
                                && order.getStatus() != Status.CANCELLED) {
                        throw new ConflictException("Session cannot be closed while order is unfinished");
                }
        }
}
