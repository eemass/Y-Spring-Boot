package com.samiul.Y.controller;

import com.samiul.Y.dto.LoginRequest;
import com.samiul.Y.dto.SignupRequest;
import com.samiul.Y.dto.UserResponse;
import com.samiul.Y.model.User;
import com.samiul.Y.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req, HttpServletResponse res) {
        UserResponse userResponse = authService.signup(req, res);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletResponse res) {
        UserResponse userResponse = authService.login(req, res);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User user) {
            return ResponseEntity.ok(new UserResponse(user));
        }

        return ResponseEntity.ok(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        authService.logout(res);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

}
