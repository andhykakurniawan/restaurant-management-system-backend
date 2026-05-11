package com.example.restaurant_be.ordersession.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.restaurant_be.ordersession.entity.OrderSession;
import com.example.restaurant_be.ordersession.entity.SessionStatus;
import com.example.restaurant_be.ordersession.repository.OrderSessionRepository;
import com.example.restaurant_be.table.entity.TableStatus;
import com.example.restaurant_be.table.repository.TableRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderSessionScheduler {

        private final OrderSessionRepository orderSessionRepository;

        private final TableRepository tableRepository;

        @Scheduled(fixedRate = 60000)
        @Transactional
        public void expireSessions() {

                List<OrderSession> sessions = orderSessionRepository
                                .findByExpiredAtBeforeAndStatusIn(
                                                LocalDateTime.now(),
                                                List.of(
                                                                SessionStatus.WAITING_ORDER,
                                                                SessionStatus.ACTIVE));

                for (OrderSession session : sessions) {

                        session.setStatus(
                                        SessionStatus.EXPIRED);

                        session.setIsActive(false);

                        session.getTable()
                                        .setStatus(
                                                        TableStatus.AVAILABLE);

                        tableRepository.save(
                                        session.getTable());

                        orderSessionRepository.save(session);
                }
        }
}