package com.samiul.Y.service;

import com.samiul.Y.dto.LoginRequest;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserResponse signup(SignupRequest req, HttpServletResponse res) {

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
        jwtUtils.setTokenCookie(res, token);

        return new UserResponse(savedUser);
    }

    public UserResponse login(LoginRequest req, HttpServletResponse res) {
        User user = userRepository.findByUsername(req.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        boolean isPasswordCorrect = passwordEncoder.matches(req.getPassword(), user.getPassword());

        if (!isPasswordCorrect) throw new IllegalArgumentException("Invalid username or password.");

        String token = jwtUtils.generateToken(user.getId().toString());
        jwtUtils.setTokenCookie(res, token);

        return new UserResponse(user);

    }
}
