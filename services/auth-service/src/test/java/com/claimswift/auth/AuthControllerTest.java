package com.claimswift.auth;

import com.claimswift.auth.dto.AuthResponse;
import com.claimswift.auth.dto.LoginResponse;
import com.claimswift.auth.dto.RegisterRequest;
import com.claimswift.auth.service.AuthService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void register_success() throws Exception {
        RegisterRequest req = new RegisterRequest("user", "pass", "mail@test.com", Set.of("USER"));

        AuthResponse res = new AuthResponse(
                "User registered successfully",
                "user",
                Set.of("USER").stream().toList(), // convert to List for AuthResponse
                null
        );

        Mockito.when(authService.register(ArgumentMatchers.any(RegisterRequest.class)))
                .thenReturn(res);

        String requestJson = """
                {
                    "username":"user",
                    "password":"pass",
                    "email":"mail@test.com",
                    "roles":["USER"]
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.roles[0]").value("USER"));
    }

    @Test
    void login_success() throws Exception {
        Mockito.when(authService.login(ArgumentMatchers.any()))
                .thenReturn(new LoginResponse("OTP sent", true));

        String requestJson = """
                {
                    "username":"user",
                    "password":"pass"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent"))
                .andExpect(jsonPath("$.mfaRequired").value(true));
    }

    @Test
    void verify_mfa_success() throws Exception {
        Mockito.when(authService.verifyMfa(ArgumentMatchers.any()))
                .thenReturn(new AuthResponse(
                        "MFA Verified",
                        "user",
                        Set.of("USER").stream().toList(),
                        "token123"
                ));

        String requestJson = """
                {
                    "username":"user",
                    "code":"123456"
                }
                """;

        mockMvc.perform(post("/auth/verify-mfa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.message").value("MFA Verified"))
                .andExpect(jsonPath("$.roles[0]").value("USER"))
                .andExpect(jsonPath("$.token").value("token123"));
    }
}