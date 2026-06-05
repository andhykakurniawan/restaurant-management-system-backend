package com.example.restaurant_be.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.example.restaurant_be.user.entity.User;
import com.example.restaurant_be.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderSessionRepository orderSessionRepository;
    private final UserRepository userRepository;

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

        if (paymentRepository.existsByOrder_IdAndStatus(order.getId(), PaymentStatus.SUCCESS)) {
            throw new ConflictException("Order sudah punya pembayaran sukses");
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
        payment.setCashier(getAuthenticatedUserOrNull());

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

    private User getAuthenticatedUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        return userRepository.findByEmail(authentication.getName())
                .orElse(null);
    }
}
