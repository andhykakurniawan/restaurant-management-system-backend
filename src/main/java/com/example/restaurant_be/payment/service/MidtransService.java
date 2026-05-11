package com.example.restaurant_be.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.restaurant_be.config.midtrans.MidtransProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MidtransService {

    private final MidtransProperties midtransProperties;

    public String createSnapToken(
            String orderId,
            Double amount,
            String customerName,
            String customerPhone) {

        RestTemplate restTemplate = new RestTemplate();

        // =========================
        // Basic Auth
        // =========================

        String auth = midtransProperties.getServerKey()
                + ":";

        String encodedAuth = Base64.getEncoder()
                .encodeToString(
                        auth.getBytes(
                                StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON);

        headers.set(
                "Authorization",
                "Basic " + encodedAuth);

        // =========================
        // Transaction Details
        // =========================

        Map<String, Object> transactionDetails = new HashMap<>();

        transactionDetails.put(
                "order_id",
                orderId);

        transactionDetails.put(
                "gross_amount",
                amount);

        // =========================
        // Customer Details
        // =========================

        Map<String, Object> customerDetails = new HashMap<>();

        customerDetails.put(
                "first_name",
                customerName);

        customerDetails.put(
                "phone",
                customerPhone);

        // =========================
        // Request Body
        // =========================

        Map<String, Object> body = new HashMap<>();

        body.put(
                "transaction_details",
                transactionDetails);

        body.put(
                "customer_details",
                customerDetails);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(
                body,
                headers);

        // =========================
        // Request Midtrans
        // =========================

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                midtransProperties.getSnapUrl(),
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {
                });

        Map<String, Object> responseBody = response.getBody();

        return responseBody
                .get("token")
                .toString();
    }
}