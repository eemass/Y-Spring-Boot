package com.samiul.Y.service;

import com.samiul.Y.dto.NotificationResponse;
import com.samiul.Y.model.Notification;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.NotificationRepository;
import com.samiul.Y.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationResponse> getNotifications(User currentUser) {
        List<Notification> notifications = notificationRepository.findByTo(currentUser.getId());

        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);

        return notifications.stream().map(notification -> {
            NotificationResponse dto = new NotificationResponse();
            dto.setId(notification.getId().toHexString());
            dto.setType(notification.getType().name());
            dto.setRead(notification.isRead());
            dto.setCreatedAt(notification.getCreatedAt());

            User fromUser = userRepository.findById(notification.getFrom())
                    .orElseThrow(() -> new RuntimeException("User not found."));

            NotificationResponse.FromUser fromDto = new NotificationResponse.FromUser();

            fromDto.setUsername(fromUser.getUsername());
            fromDto.setProfileImg(fromUser.getProfileImg());

            dto.setFrom(fromDto);

            return dto;
        }).toList();
    }

    public void deleteNotifications(User currentUser) {
        notificationRepository.deleteByTo(currentUser.getId());
        return;
    }
}
