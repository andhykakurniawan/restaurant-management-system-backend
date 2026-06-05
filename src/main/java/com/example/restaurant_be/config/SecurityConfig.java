package com.example.restaurant_be.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.restaurant_be.security.JwtAccessDeniedHandler;
import com.example.restaurant_be.security.JwtAuthenticationEntryPoint;
import com.example.restaurant_be.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Value("#{'${app.cors.allowed-origins:http://localhost:5173}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("${app.security.relaxed-permit-all:true}")
    private boolean relaxedPermitAll;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/login").permitAll()
                            .requestMatchers("/api/auth/me").authenticated();

                    if (relaxedPermitAll) {
                        auth.requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/categories/**").permitAll()
                                .requestMatchers("/api/ingredients/**").permitAll()
                                .requestMatchers("/api/menus/**").permitAll()
                                .requestMatchers("/api/menu-ingredients/**").permitAll()
                                .requestMatchers("/api/menu-categories/**").permitAll()
                                .requestMatchers("/api/inventory/**").permitAll()
                                .requestMatchers("/api/orders/**").permitAll()
                                .requestMatchers("/api/payments/**").permitAll()
                                .requestMatchers("/api/reports/**").permitAll()
                                .requestMatchers("/api/shifts/**").permitAll()
                                .requestMatchers("/api/attendances/**").permitAll()
                                .requestMatchers("/api/table-restaurants/**").permitAll()
                                .requestMatchers("/api/order-sessions/**").authenticated()
                                .requestMatchers("/api/bookings/**").permitAll();
                    } else {
                        auth.requestMatchers(HttpMethod.POST, "/api/bookings").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/bookings/*").permitAll()
                                .requestMatchers("/api/bookings/payment/notification").permitAll()
                                .requestMatchers("/api/admin/**").hasAuthority("ROLE_SUPER_ADMIN");
                    }

                    auth.anyRequest().authenticated();
                })

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
