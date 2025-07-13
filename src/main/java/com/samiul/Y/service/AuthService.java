package com.samiul.Y.service;

import com.samiul.Y.dto.SignupRequest;
import com.samiul.Y.dto.UserResponse;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.UserRepository;
import com.samiul.Y.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserResponse signup(SignupRequest req, HttpServletResponse response) {

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken.");
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Account with this email already exists.");
        }

        if (req.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        String hashedPassword = passwordEncoder.encode(req.getPassword());

        User newUser = User.builder()
                .fullName(req.getFullName())
                .username(req.getUsername())
                .email(req.getEmail())
                .password(hashedPassword)
                .profileImg("")
                .coverImg("")
                .bio("")
                .link("")
                .followers(new ArrayList<>())
                .following(new ArrayList<>())
                .likedPosts(new ArrayList<>())
                .build();

        User savedUser = userRepository.save(newUser);

        String token = jwtUtils.generateToken(savedUser.getId().toString());
        jwtUtils.setTokenCookie(response, token);

        return new UserResponse(savedUser);
    }
}
