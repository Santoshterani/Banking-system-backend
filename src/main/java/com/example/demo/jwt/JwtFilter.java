package com.example.demo.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.security.JwtUtil;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("REQUEST URI : " + request.getRequestURI());

        String token = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {

            for (Cookie cookie : cookies) {

                if ("jwt".equals(cookie.getName())) {

                    token = cookie.getValue();

                    System.out.println("JWT COOKIE FOUND");
                    break;
                }
            }
        }

        if (token != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            try {

                String email =
                        JwtUtil.extractEmail(token);

                System.out.println("EMAIL : " + email);

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);

                System.out.println(
                        "AUTHENTICATED : "
                                + userDetails.getAuthorities());

            } catch (Exception e) {

                System.out.println(
                        "JWT ERROR : "
                                + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}