package com.albaraka.digital.service.impl;

import com.albaraka.digital.model.BlacklistedToken;
import com.albaraka.digital.repository.BlacklistedTokenRepository;
import com.albaraka.digital.security.JwtUtil;
import com.albaraka.digital.service.TokenBlacklistService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void blacklistToken(String token) {
        if (token != null && !token.isEmpty() && !isBlacklisted(token)) {
            // Extract expiry date from token
            Instant expiryDate = jwtUtil.extractClaim(token, claims -> claims.getExpiration().toInstant());

            BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                    .token(token)
                    .expiryDate(expiryDate)
                    .blacklistedAt(Instant.now())
                    .build();

            blacklistedTokenRepository.save(blacklistedToken);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 * * * *") // Run every hour
    public void cleanupExpiredTokens() {
        blacklistedTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
