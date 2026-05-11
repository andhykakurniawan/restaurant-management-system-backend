package com.example.restaurant_be.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.order.entity.Order;
import com.example.restaurant_be.order.entity.Status;
import com.example.restaurant_be.order.repository.OrderRepository;
import com.example.restaurant_be.payment.dto.PaymentRequest;
import com.example.restaurant_be.payment.dto.PaymentResponse;
import com.example.restaurant_be.payment.entity.Payment;
import com.example.restaurant_be.payment.entity.PaymentStatus;
import com.example.restaurant_be.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public List<PaymentResponse> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaymentResponse findById(UUID id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        return toResponse(payment);
    }

    public PaymentResponse create(PaymentRequest request) {

        Order order = orderRepository.findByIdIncludingInactive(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Order sudah dibayar");
        }

        if (order.getStatus() == Status.CANCELLED) {
            throw new IllegalStateException("Order sudah dibatalkan");
        }

        if (order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Total order tidak valid");
        }

        if (order.getStatus() != Status.SERVED) {
            throw new IllegalStateException("Order belum siap dibayar");
        }

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(request.paymentMethod());
        payment.setStatus(PaymentStatus.SUCCESS);

        Payment savedPayment = paymentRepository.save(payment);

        order.setStatus(Status.COMPLETED);
        orderRepository.save(order);

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