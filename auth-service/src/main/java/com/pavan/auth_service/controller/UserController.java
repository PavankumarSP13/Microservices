package com.pavan.auth_service.controller;

import com.pavan.auth_service.model.UserCredentials;
import com.pavan.auth_service.service.JwtService;
import com.pavan.auth_service.service.customUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private customUserDetailsService customUserDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

//    @Autowired
//    private SessionService session;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserCredentials userCredentials) {
        return customUserDetailsService.saveUser(userCredentials);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserCredentials userCredentials) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userCredentials.getUsername(), userCredentials.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(userCredentials.getUsername());
        } else {
            return "Login Failed";
        }
    }

//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest) {
//        String token = jwtService.extractJwtToken(httpServletRequest);
//        String username = jwtService.extractUsername(token);
//        session.logout(token, username, jwtService.getExpiryMillis(t));
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/validate")
    public boolean validateToken(@RequestParam String token) {
        jwtService.validateToken(token);
        return true;
    }
}