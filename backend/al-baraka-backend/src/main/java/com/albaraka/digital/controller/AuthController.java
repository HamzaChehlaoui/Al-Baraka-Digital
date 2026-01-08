package com.albaraka.digital.controller;

import com.albaraka.digital.dto.request.LoginRequest;
import com.albaraka.digital.dto.request.RegisterRequest;
import com.albaraka.digital.dto.response.AuthResponse;
import com.albaraka.digital.service.AuthService;
import com.albaraka.digital.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;
        private final TokenBlacklistService tokenBlacklistService;

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
                return ResponseEntity.ok(authService.login(request));
        }

        @PostMapping("/register")
        public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
                return ResponseEntity.ok(authService.register(request));
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(HttpServletRequest request) {
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        tokenBlacklistService.blacklistToken(token);
                }

                return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
}
