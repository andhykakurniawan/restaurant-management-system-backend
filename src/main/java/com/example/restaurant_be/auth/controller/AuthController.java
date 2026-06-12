package com.example.restaurant_be.auth.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.restaurant_be.auth.dto.LoginRequest;
import com.example.restaurant_be.auth.dto.LoginResponse;
import com.example.restaurant_be.auth.dto.UserResponse;
import com.example.restaurant_be.security.CustomUserDetails;
import com.example.restaurant_be.auth.service.AuthService;
import com.example.restaurant_be.common.response.ApiResponse;
import com.example.restaurant_be.user.entity.User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {

        String token = authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successfully",
                        new LoginResponse(token)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body(
                    ApiResponse.error("Unauthorized"));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User retrieved successfully",
                        UserResponse.from(user)));
    }
}