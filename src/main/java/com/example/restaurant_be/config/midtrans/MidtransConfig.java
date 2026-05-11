package com.example.restaurant_be.config.midtrans;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(
        MidtransProperties.class
)
public class MidtransConfig {
}