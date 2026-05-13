package com.example.restaurant_be.booking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.booking.entity.Booking;
import com.example.restaurant_be.booking.entity.BookingStatus;
import com.example.restaurant_be.booking.repository.BookingRepository;
import com.example.restaurant_be.payment.entity.PaymentBackLog;
import com.example.restaurant_be.payment.repository.PaymentBackLogRepository;
import com.example.restaurant_be.table.entity.TableRestaurant;
import com.example.restaurant_be.table.entity.TableStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class BookingPaymentService {

    private final BookingRepository bookingRepository;

    private final PaymentBackLogRepository paymentBackLogRepository;

    private final ObjectMapper objectMapper;

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

            Booking booking = bookingRepository
                    .findByMidtransOrderId(
                            orderId)
                    .orElseThrow(() -> new RuntimeException(
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

        } catch (Exception e) {

            e.printStackTrace(); 

            throw new RuntimeException(
                    "Failed process payment callback",
                    e);
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

            table.setStatus(TableStatus.OCCUPIED);
        }

        else if (transactionStatus.equals("deny")
                ||
                transactionStatus.equals("cancel")
                ||
                transactionStatus.equals("expire")) {

            booking.setStatus(BookingStatus.CANCELLED);

            table.setStatus(TableStatus.AVAILABLE);

            booking.setCancelledAt(LocalDateTime.now());
        }
    }
}