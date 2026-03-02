package com.claimswift.auth.controller;

import com.claimswift.auth.service.JwtService;
import com.claimswift.auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenValidationController {

    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;

    @PostMapping("/validate")
    public Map<String,Object> validate(@RequestHeader("Authorization") String header){

        if(header == null || !header.startsWith("Bearer "))
            return Map.of("valid", false);

        String token = header.substring(7);

        if(blacklistService.isBlacklisted(token))
            return Map.of("valid", false);

        if(!jwtService.isValid(token))
            return Map.of("valid", false);

        return Map.of(
                "valid", true,
                "username", jwtService.extractUsername(token),
                "roles", jwtService.extractRoles(token)
        );
    }
}