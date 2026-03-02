package com.claimswift.auth.controller;

import com.claimswift.auth.dto.*;
import com.claimswift.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req){
        return service.register(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req){
        return service.login(req);
    }

    @PostMapping("/verify-mfa")
    public AuthResponse verify(@Valid @RequestBody MfaVerifyRequest req){
        return service.verifyMfa(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value="Authorization", required=false) String header){

        service.logout(header);
        return ResponseEntity.ok("Logged out");
    }

    /* ADMIN ONLY */
    @PostMapping("/unlock/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public AuthResponse unlock(@PathVariable String username){
        return service.unlockAccount(username);
    }
}