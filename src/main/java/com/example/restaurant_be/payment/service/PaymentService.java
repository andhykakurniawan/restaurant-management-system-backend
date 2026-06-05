package com.example.restaurant_be.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.common.exception.ConflictException;
import com.example.restaurant_be.common.exception.NotFoundException;
import com.example.restaurant_be.order.entity.Order;
import com.example.restaurant_be.order.entity.Status;
import com.example.restaurant_be.order.repository.OrderRepository;
import com.example.restaurant_be.ordersession.entity.OrderSession;
import com.example.restaurant_be.ordersession.entity.SessionStatus;
import com.example.restaurant_be.ordersession.repository.OrderSessionRepository;
import com.example.restaurant_be.payment.dto.PaymentRequest;
import com.example.restaurant_be.payment.dto.PaymentResponse;
import com.example.restaurant_be.payment.entity.Payment;
import com.example.restaurant_be.payment.entity.PaymentStatus;
import com.example.restaurant_be.payment.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderSessionRepository orderSessionRepository;

    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaymentResponse findById(UUID id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse create(PaymentRequest request) {

        Order order = orderRepository.findByIdIncludingInactive(request.orderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() == Status.COMPLETED) {
            throw new ConflictException("Order sudah dibayar");
        }

        if (order.getStatus() == Status.CANCELLED) {
            throw new ConflictException("Order sudah dibatalkan");
        }

        if (order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ConflictException("Total order tidak valid");
        }

        if (order.getStatus() != Status.SERVED) {
            throw new ConflictException("Order belum siap dibayar");
        }

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(request.paymentMethod());
        payment.setStatus(PaymentStatus.SUCCESS);

        Payment savedPayment = paymentRepository.save(payment);

        order.setStatus(Status.COMPLETED);
        orderRepository.save(order);

        OrderSession session = order.getOrderSession();
        session.setStatus(SessionStatus.PAID);
        orderSessionRepository.save(session);

        return toResponse(savedPayment);
    }

    private PaymentResponse toResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getMethod().name(),
                payment.getStatus().name());
    }
}
