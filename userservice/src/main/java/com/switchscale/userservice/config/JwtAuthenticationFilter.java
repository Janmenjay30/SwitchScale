package com.switchscale.userservice.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchscale.userservice.dto.AuthResponseDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTConfig jwtConfig;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JWTConfig jwtConfig, ObjectMapper objectMapper) {
        this.jwtConfig = jwtConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "/users/register".equals(path) || "/users/login".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
            return;
        }

        if (!jwtConfig.validationToken(authorizationHeader)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtConfig.extractEMail(authorizationHeader);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, java.util.List.of());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(AuthResponseDto.error(message)));
    }
}