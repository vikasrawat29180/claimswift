package com.claimswift.auth;

import com.claimswift.auth.config.JwtAuthFilter;
import com.claimswift.auth.service.JwtService;
import com.claimswift.auth.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.*;

import java.util.List;

import static org.mockito.Mockito.*;

class JwtFilterTest {

    @Test
    void validToken_setsAuthentication() throws Exception {

        JwtService jwtService = mock(JwtService.class);
        TokenBlacklistService blacklist = mock(TokenBlacklistService.class);

        JwtAuthFilter filter = new JwtAuthFilter(jwtService, blacklist);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization","Bearer token");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(blacklist.isBlacklisted("token")).thenReturn(false);
        when(jwtService.isTokenValid("token")).thenReturn(true);
        when(jwtService.extractUsername("token")).thenReturn("john");
        when(jwtService.extractRoles("token")).thenReturn(List.of("USER"));

        filter.doFilter(req,res,chain);

        verify(chain).doFilter(req,res);
    }
}

