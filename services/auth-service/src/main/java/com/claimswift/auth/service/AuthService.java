package com.claimswift.auth.service;

import com.claimswift.auth.dto.*;
import com.claimswift.auth.entity.*;
import com.claimswift.auth.exception.*;
import com.claimswift.auth.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final LoginAuditRepository loginAuditRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;
    private final EmailService emailService;

    private static final int MAX_ATTEMPTS = 3;

    /* ================= REGISTER ================= */
    public AuthResponse register(RegisterRequest request) {

        if (userRepo.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already taken");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAccountNonLocked(true);
        user.setMfaEnabled(true);
        user.setFailedAttempts(0);

        /* SECURITY FIX → force USER role */
        Role role = roleRepo.findByName("USER")
                .orElseGet(() -> roleRepo.save(new Role("USER")));

        user.setRoles(Set.of(role));
        userRepo.save(user);

        return new AuthResponse(
                "User registered successfully",
                user.getUsername(),
                List.of("USER"),
                null
        );
    }

    /* ================= LOGIN ================= */
    public LoginResponse login(LoginRequest request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    saveAudit(request.getUsername(), false, "User not found");
                    return new InvalidCredentialsException();
                });

        if (!user.isAccountNonLocked()) {
            saveAudit(user.getUsername(), false, "Account locked");
            throw new AccountLockedException();
        }

        if (!encoder.matches(request.getPassword(), user.getPassword())) {

            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                user.setAccountNonLocked(false);
                user.setLockTime(new Date());
                userRepo.save(user);

                saveAudit(user.getUsername(), false, "Account locked after max attempts");
                throw new AccountLockedException();
            }

            userRepo.save(user);
            saveAudit(user.getUsername(), false, "Invalid password");
            throw new InvalidCredentialsException();
        }

        user.setFailedAttempts(0);

        /* MFA DISABLED */
        if (!user.isMfaEnabled()) {
            saveAudit(user.getUsername(), true, "Login success (no MFA)");
            userRepo.save(user);
            return new LoginResponse("Login successful", false);
        }

        /* OTP GENERATION */
        String otp = String.valueOf(100000 + new SecureRandom().nextInt(900000));
        String hash = encoder.encode(otp);

        user.setOtpHash(hash);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepo.save(user);

        /* EMAIL SEND SAFE */
        try {
            emailService.sendOtp(user.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email");
        }

        saveAudit(user.getUsername(), true, "OTP generated and sent");

        return new LoginResponse("OTP sent to email", true);
    }

    /* ================= VERIFY MFA ================= */
    public AuthResponse verifyMfa(MfaVerifyRequest request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException(request.getUsername()));

        if (user.getOtpHash() == null ||
                user.getOtpExpiry() == null ||
                user.getOtpExpiry().isBefore(LocalDateTime.now()) ||
                !encoder.matches(request.getCode(), user.getOtpHash())) {

            saveAudit(user.getUsername(), false, "Invalid OTP");
            throw new InvalidOtpException();
        }

        List<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        String token = jwtService.generateToken(user.getUsername(), roleNames);

        user.setOtpHash(null);
        user.setOtpExpiry(null);
        userRepo.save(user);

        saveAudit(user.getUsername(), true, "MFA verified");

        return new AuthResponse(
                "MFA Verified Successfully",
                user.getUsername(),
                roleNames,
                token
        );
    }

    /* ================= LOGOUT ================= */
    public void logout(String header) {

        if (header == null || !header.startsWith("Bearer "))
            return;

        String token = header.substring(7);
        blacklistService.blacklist(token);
    }

    /* ================= UNLOCK ================= */
    public AuthResponse unlockAccount(String username) {

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setLockTime(null);

        userRepo.save(user);

        saveAudit(username, true, "Account unlocked");

        return new AuthResponse(
                "Account unlocked successfully",
                username,
                null,
                null
        );
    }

    /* ================= AUDIT ================= */
    private void saveAudit(String username, boolean success, String message) {

        loginAuditRepo.save(
                LoginAudit.builder()
                        .username(username)
                        .success(success)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}