package com.example.restaurant_be.config.midtrans;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "midtrans")
public class MidtransProperties {

    private String serverKey;

    private String clientKey;

    private String snapUrl;
}