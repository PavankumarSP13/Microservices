package com.pavan.questionservice.config;

import com.pavan.questionservice.exceptions.UnauthorizedException;
import com.pavan.questionservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);

            // ✅ Set Authentication object in SecurityContext
            // claims.getSubject() → Sets the principal, usually the username or user ID.
            // null for credentials → Because you’re not re-authenticating with a password, just validating a JWT.
            //Collections.emptyList() → No authorities/roles provided. Could be enhanced later.

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), null, Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized access: {}", e.getMessage());
            handleErrorResponse(httpServletResponse, e.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        } catch (Exception e) {
            logger.error("Unexpected error in authentication filter", e);
            handleErrorResponse(httpServletResponse, "Unauthorized access", HttpStatus.UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void handleErrorResponse(HttpServletResponse httpServletResponse, String message, HttpStatus status) throws IOException {
        httpServletResponse.setStatus(status.value());
        httpServletResponse.setContentType("application/json");
        String body = String.format("{\"error\": \"%s\"}", message);
        httpServletResponse.getWriter().write(body);
    }

}


