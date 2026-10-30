package com.example.demo.config;

import com.example.demo.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	http
        .cors(cors -> {})
        .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/auth/login", "/auth/logout", "/auth/forgot-password", "/auth/reset-password")
                .permitAll()

                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                .permitAll()

                .requestMatchers("/actuator/health", "/actuator/info")
                .permitAll()

                .requestMatchers("/account/**")
                .authenticated()

                // Fine-grained role checks (who can call which endpoint) live on each
                // controller method via @PreAuthorize - branch-scoped roles like CASHIER
                // or LOAN_OFFICER need access to some /admin/** paths, so a blanket
                // hasRole("ADMIN") here would reject them before @PreAuthorize even runs.
                .requestMatchers("/admin/**")
                .authenticated()

                .anyRequest()
                .authenticated()
            )

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
