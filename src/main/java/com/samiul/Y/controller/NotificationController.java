package com.samiul.Y.controller;

import com.samiul.Y.dto.NotificationResponse;
import com.samiul.Y.model.Notification;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.NotificationRepository;
import com.samiul.Y.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/")
    public ResponseEntity<?> getNotifications(@AuthenticationPrincipal User currentUser) {
        List<NotificationResponse> notifications = notificationService.getNotifications(currentUser);
        return ResponseEntity.ok(notifications);
    }

}
