package com.example.restaurant_be.ordersession.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.restaurant_be.ordersession.entity.SessionStatus;
import com.example.restaurant_be.ordersession.repository.OrderSessionRepository;
import com.example.restaurant_be.table.repository.TableRepository;

class OrderSessionSchedulerTest {

    @Test
    @SuppressWarnings("unchecked")
    void onlyExpiresWaitingOrderSessions() {
        OrderSessionRepository orderSessionRepository = mock(OrderSessionRepository.class);
        TableRepository tableRepository = mock(TableRepository.class);

        when(orderSessionRepository.findByExpiredAtBeforeAndStatusIn(
                any(LocalDateTime.class),
                any()))
                .thenReturn(List.of());

        OrderSessionScheduler scheduler = new OrderSessionScheduler(
                orderSessionRepository,
                tableRepository);

        scheduler.expireSessions();

        ArgumentCaptor<List<SessionStatus>> statuses = ArgumentCaptor.forClass(List.class);

        verify(orderSessionRepository).findByExpiredAtBeforeAndStatusIn(
                any(LocalDateTime.class),
                statuses.capture());

        assertEquals(List.of(SessionStatus.WAITING_ORDER), statuses.getValue());
    }
}
