package com.example.restaurant_be.booking.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.example.restaurant_be.booking.repository.BookingRepository;
import com.example.restaurant_be.common.exception.BadRequestException;
import com.example.restaurant_be.config.midtrans.MidtransProperties;
import com.example.restaurant_be.payment.repository.PaymentBackLogRepository;
import com.example.restaurant_be.payment.entity.PaymentBackLog;

import tools.jackson.databind.ObjectMapper;

class BookingPaymentServiceTest {

    @Test
    void rejectsInvalidMidtransSignature() {
        BookingRepository bookingRepository = mock(BookingRepository.class);
        PaymentBackLogRepository paymentBackLogRepository = mock(PaymentBackLogRepository.class);

        BookingPaymentService service = new BookingPaymentService(
                bookingRepository,
                paymentBackLogRepository,
                mock(ObjectMapper.class),
                midtransProperties());

        Map<String, Object> payload = basePayload();
        payload.put("signature_key", "bad-signature");

        assertThrows(
                BadRequestException.class,
                () -> service.handleNotification(payload));

        verify(paymentBackLogRepository, never()).save(any(PaymentBackLog.class));
        verify(bookingRepository, never()).findByMidtransOrderId(anyString());
    }

    @Test
    void ignoresDuplicateTransactionStatusCallback() throws Exception {
        BookingRepository bookingRepository = mock(BookingRepository.class);
        PaymentBackLogRepository paymentBackLogRepository = mock(PaymentBackLogRepository.class);

        when(paymentBackLogRepository.existsByTransactionIdAndTransactionStatus("trx-1", "settlement"))
                .thenReturn(true);

        BookingPaymentService service = new BookingPaymentService(
                bookingRepository,
                paymentBackLogRepository,
                mock(ObjectMapper.class),
                midtransProperties());

        Map<String, Object> payload = basePayload();
        payload.put("signature_key", signature(payload));

        service.handleNotification(payload);

        verify(paymentBackLogRepository, never()).save(any(PaymentBackLog.class));
        verify(bookingRepository, never()).findByMidtransOrderId(anyString());
    }

    private MidtransProperties midtransProperties() {
        MidtransProperties properties = new MidtransProperties();
        properties.setServerKey("test-server-key");
        properties.setVerifySignature(true);
        return properties;
    }

    private Map<String, Object> basePayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("order_id", "ORDER-1234");
        payload.put("status_code", "200");
        payload.put("gross_amount", "10000.00");
        payload.put("transaction_status", "settlement");
        payload.put("payment_type", "bank_transfer");
        payload.put("fraud_status", "accept");
        payload.put("transaction_id", "trx-1");
        return payload;
    }

    private String signature(Map<String, Object> payload) throws Exception {
        String raw = payload.get("order_id").toString()
                + payload.get("status_code").toString()
                + payload.get("gross_amount").toString()
                + "test-server-key";

        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
    }
}
