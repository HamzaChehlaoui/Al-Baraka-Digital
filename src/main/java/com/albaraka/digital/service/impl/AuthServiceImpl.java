package com.albaraka.digital.service.impl;

import com.albaraka.digital.dto.request.LoginRequest;
import com.albaraka.digital.dto.request.RegisterRequest;
import com.albaraka.digital.dto.response.AuthResponse;
import com.albaraka.digital.exception.InvalidOperationException;
import com.albaraka.digital.exception.ResourceNotFoundException;
import com.albaraka.digital.model.Account;
import com.albaraka.digital.model.User;
import com.albaraka.digital.model.enums.Role;
import com.albaraka.digital.repository.UserRepository;
import com.albaraka.digital.security.JwtUtil;
import com.albaraka.digital.service.AccountService;
import com.albaraka.digital.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final AccountService accountService;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new InvalidOperationException("Account is deactivated");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        String accountNumber = null;
        if (user.getAccounts() != null && !user.getAccounts().isEmpty()) {
            accountNumber = user.getAccounts().get(0).getAccountNumber();
        }

        String accessToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .email(user.getEmail())
                .fullName(user.getName())
                .role(user.getRole())
                .accountNumber(accountNumber)
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidOperationException("Email already exists");
        }

        User user = User.builder()
                .name(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        Account account = accountService.createAccountForUser(savedUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .email(user.getEmail())
                .fullName(user.getName())
                .role(user.getRole())
                .accountNumber(account.getAccountNumber())
                .build();
    }
}
