package com.samiul.Y.controller;

import com.samiul.Y.dto.UserResponse;
import com.samiul.Y.dto.UserUpdateRequest;
import com.samiul.Y.model.User;
import com.samiul.Y.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/follow/{id}")
    public ResponseEntity<?> followUnfollowUser(@PathVariable String id, @AuthenticationPrincipal User currentUser) {
        String message = userService.followUnfollowUser(id, currentUser);

        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest request, @AuthenticationPrincipal User currentUser) throws IOException {
        UserResponse updatedUser = userService.updateUser(request, currentUser);

        return ResponseEntity.ok(updatedUser);
    }
}
