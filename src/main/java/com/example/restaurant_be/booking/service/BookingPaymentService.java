package com.example.restaurant_be.booking.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.audit.service.AuditLogService;
import com.example.restaurant_be.booking.entity.Booking;
import com.example.restaurant_be.booking.entity.BookingStatus;
import com.example.restaurant_be.booking.repository.BookingRepository;
import com.example.restaurant_be.common.exception.BadRequestException;
import com.example.restaurant_be.common.exception.NotFoundException;
import com.example.restaurant_be.config.midtrans.MidtransProperties;
import com.example.restaurant_be.payment.entity.PaymentBackLog;
import com.example.restaurant_be.payment.repository.PaymentBackLogRepository;
import com.example.restaurant_be.table.entity.TableRestaurant;
import com.example.restaurant_be.table.entity.TableStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingPaymentService {

    private final BookingRepository bookingRepository;

    private final PaymentBackLogRepository paymentBackLogRepository;

    private final ObjectMapper objectMapper;

    private final MidtransProperties midtransProperties;
    private final AuditLogService auditLogService;

    @Transactional
    public void handleNotification(
            Map<String, Object> payload) {

        try {

            String orderId = (String) payload.get("order_id");

            String transactionStatus = (String) payload.get(
                    "transaction_status");

            String paymentType = (String) payload.get(
                    "payment_type");

            String fraudStatus = (String) payload.get(
                    "fraud_status");

            String transactionId = (String) payload.get(
                    "transaction_id");

            String grossAmount = (String) payload.get(
                    "gross_amount");

            validateSignature(payload, orderId, grossAmount);

            if (transactionId != null
                    && paymentBackLogRepository.existsByTransactionIdAndTransactionStatus(
                            transactionId,
                            transactionStatus)) {
                return;
            }

            Booking booking = bookingRepository
                    .findByMidtransOrderId(
                            orderId)
                    .orElseThrow(() -> new NotFoundException(
                            "Booking not found"));

            PaymentBackLog backlog = new PaymentBackLog();

            backlog.setMidtransOrderId(orderId);
            backlog.setTransactionStatus(transactionStatus);
            backlog.setPaymentType(paymentType);
            backlog.setFraudStatus(fraudStatus);
            backlog.setTransactionId(transactionId);
            backlog.setGrossAmount(new BigDecimal(grossAmount));
            backlog.setReceivedAt(LocalDateTime.now());
            backlog.setRawResponse(objectMapper.writeValueAsString(payload));
            paymentBackLogRepository.save(backlog);

            TableRestaurant table = booking.getTable();
            handleBookingStatus(booking, transactionStatus, fraudStatus, table);
            auditLogService.log(
                    "BOOKING_PAYMENT_NOTIFICATION",
                    "Booking",
                    booking.getId(),
                    "status=" + transactionStatus + ", transactionId=" + transactionId);

        } catch (BadRequestException | NotFoundException e) {
            throw e;
        } catch (Exception e) {

            log.error("Failed process Midtrans payment callback", e);

            throw new RuntimeException(
                    "Failed process payment callback",
                    e);
        }
    }

    private void validateSignature(Map<String, Object> payload, String orderId, String grossAmount) {
        if (!midtransProperties.isVerifySignature()) {
            return;
        }

        String statusCode = (String) payload.get("status_code");
        String signatureKey = (String) payload.get("signature_key");

        if (orderId == null || statusCode == null || grossAmount == null || signatureKey == null) {
            throw new BadRequestException("Invalid Midtrans notification payload");
        }

        String rawSignature = orderId
                + statusCode
                + grossAmount
                + midtransProperties.getServerKey();

        String expectedSignature = sha512(rawSignature);

        if (!expectedSignature.equalsIgnoreCase(signatureKey)) {
            throw new BadRequestException("Invalid Midtrans signature");
        }
    }

    private String sha512(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 algorithm is not available", e);
        }
    }

    private void handleBookingStatus(Booking booking, String transactionStatus, String fraudStatus,
            TableRestaurant table) {

        if (transactionStatus.equals("capture")) {

            if (fraudStatus.equals("challenge")) {

                booking.setStatus(BookingStatus.PENDING);

            } else if (fraudStatus.equals("accept")) {

                booking.setStatus(BookingStatus.CONFIRMED);

                booking.setPaidAt(LocalDateTime.now());

                table.setStatus(TableStatus.RESERVED);
            }
        }

        else if (transactionStatus.equals("settlement")) {

            booking.setStatus(BookingStatus.CONFIRMED);

            booking.setPaidAt(LocalDateTime.now());

            table.setStatus(TableStatus.RESERVED);
        }

        else if (transactionStatus.equals("pending")) {

            booking.setStatus(BookingStatus.WAITING_PAYMENT);
        }

        else if (transactionStatus.equals("expire")) {

            booking.setStatus(BookingStatus.EXPIRED);

            table.setStatus(TableStatus.AVAILABLE);

            booking.setCancelledAt(LocalDateTime.now());
        }

        else if (transactionStatus.equals("deny")
                ||
                transactionStatus.equals("cancel")) {

            booking.setStatus(BookingStatus.CANCELLED);

            table.setStatus(TableStatus.AVAILABLE);

            booking.setCancelledAt(LocalDateTime.now());
        }
    }
}
