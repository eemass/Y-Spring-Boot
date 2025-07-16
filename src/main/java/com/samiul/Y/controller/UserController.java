package com.samiul.Y.controller;

import com.samiul.Y.dto.UserResponse;
import com.samiul.Y.model.User;
import com.samiul.Y.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        UserResponse user = userService.getUserProfile(username);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/suggested")
    public ResponseEntity<?> getSuggestedUsers(@AuthenticationPrincipal User user) {
        List<UserResponse> suggestedUsers = userService.getSuggestedUsers(user.getId());

        return ResponseEntity.ok(suggestedUsers);
    }
}
