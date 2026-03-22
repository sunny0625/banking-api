package com.banking.banking_api.controller;

import com.banking.banking_api.dto.AuthResponse;
import com.banking.banking_api.dto.LoginRequest;
import com.banking.banking_api.dto.RegisterRequest;
import com.banking.banking_api.model.User;
import com.banking.banking_api.repository.UserRepository;
import com.banking.banking_api.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login and register endpoints")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── POST /api/auth/login ──────────────────────────────────────────────
    // FIX: Was an empty method body — added full authentication logic.
    // authenticate() throws BadCredentialsException if wrong credentials.
    // On success, generate a JWT and return it to the client.
    @PostMapping("/login")
    @Operation(summary = "Login with username and password — returns JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getUsername(),
                            req.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Invalid username or password", null));
        }

        String token = jwtUtil.generateToken(req.getUsername());
        return ResponseEntity.ok(
                new AuthResponse(token, "Login successful", req.getUsername())
        );
    }

    // ── POST /api/auth/register ───────────────────────────────────────────
    // FIX: RegisterRequest class was missing — now created in dto/ package.
    // FIX: Was an empty method body — added full registration logic.
    // Password is Crypt-encoded before saving. Never store plain text.
    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new AuthResponse(null, "Username already exists", null));
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole() != null ? req.getRole() : "ROLE_USER");
        userRepository.save(user);

        String token = jwtUtil.generateToken(req.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse(token, "User registered successfully", req.getUsername()));
    }
}
