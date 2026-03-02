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
import java.util.*;

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

    private static final Set<String> ALLOWED_ROLES =
            Set.of("USER","MANAGER","ADMIN");


    /* ================= REGISTER ================= */
    public AuthResponse register(RegisterRequest request) {

        if (userRepo.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already exists");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAccountNonLocked(true);
        user.setMfaEnabled(true);
        user.setFailedAttempts(0);

        /* ===== ROLE VALIDATION ===== */
        Set<String> requestedRoles = request.getRoles();

        if (requestedRoles == null || requestedRoles.isEmpty()) {
            requestedRoles = Set.of("USER");
        }

        for(String r : requestedRoles){
            if(!ALLOWED_ROLES.contains(r.toUpperCase()))
                throw new RuntimeException("Invalid role: " + r);
        }

        /* ===== ROLE ASSIGNMENT ===== */
        Set<Role> roles = requestedRoles.stream()
                .map(roleName ->
                        roleRepo.findByName(roleName.toUpperCase())
                                .orElseGet(() ->
                                        roleRepo.save(new Role(roleName.toUpperCase()))
                                )
                )
                .collect(java.util.stream.Collectors.toSet());

        user.setRoles(roles);
        userRepo.save(user);

        return new AuthResponse(
                "User registered successfully",
                user.getUsername(),
                roles.stream().map(Role::getName).toList(),
                null
        );
    }


    /* ================= LOGIN ================= */
    public LoginResponse login(LoginRequest request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isAccountNonLocked())
            throw new AccountLockedException();


        /* ===== PASSWORD CHECK ===== */
        if (!encoder.matches(request.getPassword(), user.getPassword())) {

            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                user.setAccountNonLocked(false);
                user.setLockTime(new Date());
                userRepo.save(user);
                throw new AccountLockedException();
            }

            userRepo.save(user);
            throw new InvalidCredentialsException();
        }

        user.setFailedAttempts(0);

        /* ===== NO MFA ===== */
        if (!user.isMfaEnabled()) {

            String token = jwtService.generateToken(
                    user.getUsername(),
                    user.getRoles().stream().map(Role::getName).toList()
            );

            return new LoginResponse(token,false);
        }

        /* ===== MFA OTP GENERATION ===== */

        String otp = String.valueOf(100000 + new SecureRandom().nextInt(900000));

        user.setOtpHash(encoder.encode(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepo.save(user);

        emailService.sendOtp(user.getEmail(), otp);

        return new LoginResponse("OTP sent",true);
    }


    /* ================= VERIFY MFA ================= */
    public AuthResponse verifyMfa(MfaVerifyRequest request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException(request.getUsername()));

        if (user.getOtpHash() == null ||
            user.getOtpExpiry() == null ||
            user.getOtpExpiry().isBefore(LocalDateTime.now()) ||
            !encoder.matches(request.getCode(), user.getOtpHash()))
        {
            throw new InvalidOtpException();
        }

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        String token = jwtService.generateToken(user.getUsername(), roles);

        /* CLEAR OTP AFTER SUCCESS */
        user.setOtpHash(null);
        user.setOtpExpiry(null);
        userRepo.save(user);

        return new AuthResponse(
                "MFA verified",
                user.getUsername(),
                roles,
                token
        );
    }


    /* ================= LOGOUT ================= */
    public void logout(String header){

        if(header == null || !header.startsWith("Bearer "))
            return;

        blacklistService.blacklist(header.substring(7));
    }


    /* ================= UNLOCK ================= */
    public AuthResponse unlockAccount(String username){

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setLockTime(null);

        userRepo.save(user);

        return new AuthResponse(
                "Account unlocked",
                username,
                null,
                null
        );
    }


    /* ================= VALIDATE TOKEN ================= */
    public AuthResponse validateToken(String header) throws InvalidTokenException{

        if(header == null || !header.startsWith("Bearer "))
            throw new InvalidTokenException();

        String token = header.substring(7);

        if(blacklistService.isBlacklisted(token))
            throw new InvalidTokenException();

        if(!jwtService.isValid(token))
            throw new InvalidTokenException();

        String username = jwtService.extractUsername(token);
        List<String> roles = jwtService.extractRoles(token);

        return new AuthResponse(
                "Token valid",
                username,
                roles,
                token
        );
    }
}