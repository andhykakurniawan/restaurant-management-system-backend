package com.example.restaurant_be.config;

import com.example.restaurant_be.user.entity.Role;
import com.example.restaurant_be.user.entity.User;
import com.example.restaurant_be.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String email = "admin@restaurant.com";


        if (userRepository.findByEmail(email).isEmpty()) {

            User user = new User();
            user.setUsername("Super Admin");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRole(Role.SUPER_ADMIN);

            userRepository.save(user);

            log.info("SUPER_ADMIN created.");
        } else {
            log.info("SUPER_ADMIN already exists.");
        }
    }
}
