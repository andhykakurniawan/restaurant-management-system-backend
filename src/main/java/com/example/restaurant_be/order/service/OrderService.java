package com.example.restaurant_be.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.order.dto.OrderRequest;
import com.example.restaurant_be.order.dto.OrderResponse;
import com.example.restaurant_be.order.entity.Order;
import com.example.restaurant_be.order.entity.Status;
import com.example.restaurant_be.order.repository.OrderRepository;
import com.example.restaurant_be.common.exception.ConflictException;
import com.example.restaurant_be.common.exception.NotFoundException;
import com.example.restaurant_be.ordersession.entity.OrderSession;
import com.example.restaurant_be.ordersession.entity.SessionStatus;
import com.example.restaurant_be.ordersession.repository.OrderSessionRepository;
import com.example.restaurant_be.user.entity.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderSessionRepository orderSessionRepository;

    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse findById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        return toResponse(order);
    }

    public OrderResponse findBySessionId(UUID sessionId) {
        Order order = orderRepository.findByOrderSession_Id(sessionId)
                .orElseThrow(() -> new NotFoundException("Order not found for session"));

        return toResponse(order);
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {

        OrderSession session = orderSessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new NotFoundException("Order session not found"));

        if (!Boolean.TRUE.equals(session.getIsActive())
                || session.getStatus() == SessionStatus.CLOSED
                || session.getStatus() == SessionStatus.EXPIRED
                || session.getStatus() == SessionStatus.CANCELLED) {
            throw new ConflictException("Order session is not active");
        }

        if (orderRepository.findByOrderSession_Id(session.getId()).isPresent()) {
            throw new ConflictException("Order session already has an order");
        }

        User user = session.getCreatedBy();

        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setCreatedBy(user);
        order.setOrderSession(session);
        order.setStatus(Status.OPEN);
        order.setTotalAmount(BigDecimal.ZERO);

        Order saved = orderRepository.save(order);

        session.setStatus(SessionStatus.ACTIVE);
        orderSessionRepository.save(session);

        return toResponse(saved);
    }

    public void delete(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != Status.OPEN &&
                order.getStatus() != Status.CONFIRMED) {

            throw new ConflictException("Order cannot be deleted at this stage");
        }

        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponse restore(UUID id) {

        Order order = orderRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (Boolean.TRUE.equals(order.getIsActive())) {
            throw new ConflictException("Order already active");
        }

        orderRepository.restoreById(id);

        return toResponse(orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restore failed")));
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getOrderSession().getId(),
                order.getOrderSession().getTable().getId(),
                order.getOrderSession().getTable().getTableNumber(),
                order.getCreatedBy().getId(),
                order.getCreatedBy().getUsername(),
                order.getStatus(),
                order.getTotalAmount());
    }

    private String generateOrderCode() {

        String date = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE);

        String prefix = "ORD-" + date;

        Optional<Order> lastOrder = orderRepository.findTopByOrderCodeStartingWithOrderByOrderCodeDesc(prefix);

        int nextNumber = 1;

        if (lastOrder.isPresent()) {

            String lastCode = lastOrder.get().getOrderCode();

            String lastNumber = lastCode.substring(lastCode.length() - 4);

            nextNumber = Integer.parseInt(lastNumber) + 1;
        }

        return prefix + "-" + String.format("%04d", nextNumber);
    }

    @Transactional
    public OrderResponse confirmOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != Status.OPEN) {
            throw new ConflictException("Only OPEN orders can be confirmed");
        }

        order.setStatus(Status.CONFIRMED);

        Order updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != Status.OPEN &&
            order.getStatus() != Status.CONFIRMED) {
            throw new ConflictException("Order cannot be cancelled at this stage");
        }

        order.setStatus(Status.CANCELLED);

        Order updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse completeOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != Status.SERVED) {
            throw new ConflictException("Only SERVED orders can be completed");
        }

        order.setStatus(Status.COMPLETED);

        Order updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse preparingOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        System.out.println("Current status: " + order.getStatus());

        if (order.getStatus() != Status.CONFIRMED) {
            throw new ConflictException("Only CONFIRMED orders can be prepared");
        }

        order.setStatus(Status.PREPARING);

        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse readyOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != Status.PREPARING) {
            throw new ConflictException("Only PREPARING orders can be marked as ready");
        }

        order.setStatus(Status.READY);

        Order updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse servedOrder(UUID id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != Status.READY) {
            throw new ConflictException("Only READY orders can be marked as served");
        }

        order.setStatus(Status.SERVED);

        Order updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }
}
