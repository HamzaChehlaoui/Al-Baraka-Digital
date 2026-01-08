package com.albaraka.digital.config;

import com.albaraka.digital.model.Account;
import com.albaraka.digital.model.User;
import com.albaraka.digital.model.enums.Role;
import com.albaraka.digital.repository.AccountRepository;
import com.albaraka.digital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        System.out.println("🚀 DataSeeder: Starting...");
        try {
            seedUsers();
        } catch (Exception e) {
            System.err.println("❌ DataSeeder FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            // Create Admin
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@albaraka.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);

            // Create Agent
            User agent = User.builder()
                    .name("Test Agent")
                    .email("agent@albaraka.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.AGENT)
                    .active(true)
                    .build();
            User savedAgent = userRepository.save(agent);
            createAccount(savedAgent);

            // Create Client
            User client = User.builder()
                    .name("Test Client")
                    .email("client@albaraka.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.CLIENT)
                    .active(true)
                    .build();
            User savedClient = userRepository.save(client);
            createAccount(savedClient);

            System.out.println(
                    "✅ Data Seeding Completed: Users created (admin, agent, client) with password 'password123'");
        }
    }

    private void createAccount(User user) {
        Account account = Account.builder()
                .accountNumber("ALB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();
        accountRepository.save(account);
    }
}
