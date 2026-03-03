package com.example.demo.config;

import com.example.demo.client.AuthClient;
import com.example.demo.dto.AuthValidationResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthClient authClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")){

            AuthValidationResponse result =
                    authClient.validate(header);

            if(result != null && result.isValid()){

                var authorities =
                        result.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_"+r))
                                .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                result.getUsername(),
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(auth);
            }
        }

        filterChain.doFilter(request,response);
    }
}