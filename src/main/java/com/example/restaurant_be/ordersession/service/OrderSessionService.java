package com.example.restaurant_be.ordersession.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.restaurant_be.ordersession.dto.OrderSessionRequest;
import com.example.restaurant_be.ordersession.dto.OrderSessionResponse;
import com.example.restaurant_be.ordersession.entity.OrderSession;
import com.example.restaurant_be.ordersession.entity.SessionStatus;
import com.example.restaurant_be.ordersession.repository.OrderSessionRepository;
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

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        TableRestaurant table = tableRepository.findByIdIncludingInactive(request.tableId())
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        boolean hasActiveSession = orderSessionRepository
                .existsByTableAndIsActiveTrue(
                        table);

        if (hasActiveSession) {

            throw new RuntimeException(
                    "Table still has active session");
        }

        if (table.getStatus() == TableStatus.OCCUPIED) {
            throw new RuntimeException("Meja sedang digunakan");
        }

        table.setStatus(TableStatus.RESERVED);

        tableRepository.save(table);

        String sessionToken = UUID.randomUUID().toString();

        LocalDateTime startedAt = LocalDateTime.now();

        LocalDateTime ExpiredAt = startedAt.plusMinutes(15);

        OrderSession session = new OrderSession();
        session.setTable(table);
        session.setCreatedBy(user);
        session.setSessionToken(sessionToken);
        session.setIsActive(true);
        session.setStartedAt(startedAt);
        session.setStatus(SessionStatus.WAITING_ORDER);
        session.setExpiredAt(ExpiredAt);
        session.setClosedAt(null);

        OrderSession saved = orderSessionRepository.save(session);

        return toResponse(saved);
    }

    private OrderSessionResponse toResponse(OrderSession session) {
        return new OrderSessionResponse(
                session.getId(),
                session.getTable().getId(),
                session.getTable().getTableNumber(),
                session.getCreatedBy().getId(),
                session.getCreatedBy().getUsername(),
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
                .orElseThrow(() -> new RuntimeException(
                        "Session not found"));

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
}