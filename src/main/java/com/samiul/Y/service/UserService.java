package com.samiul.Y.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.samiul.Y.dto.UserResponse;
import com.samiul.Y.dto.UserUpdateRequest;
import com.samiul.Y.model.Notification;
import com.samiul.Y.model.NotificationType;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.NotificationRepository;
import com.samiul.Y.repository.UserRepository;
import com.samiul.Y.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final MongoTemplate mongoTemplate;
    private final Cloudinary cloudinary;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist."));

        return new UserResponse(user);
    }

    public List<UserResponse> getSuggestedUsers(ObjectId myId) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").ne(myId)),
                Aggregation.sample(10)
        );

        AggregationResults<User> results = mongoTemplate.aggregate(agg, "users", User.class);
        List<User> randomUsers = results.getMappedResults();

        User me = userRepository.findById(myId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return randomUsers.stream()
                .filter(u -> !me.getFollowing().contains(u.getId()))
                .limit(4)
                .map(UserResponse::new)
                .toList();
    }

    public String followUnfollowUser(String targetUserId, User currentUser) {
        if (targetUserId.equals(currentUser.getId().toHexString())) {
            throw new IllegalArgumentException("You cannot follow this user.");
        }

        ObjectId targetId = new ObjectId(targetUserId);
        User userToModify = userRepository.findById(targetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist."));

        User freshCurrentUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User not found."));

        boolean isFollowing = freshCurrentUser.getFollowing().contains(targetId);

        if (isFollowing) {
            freshCurrentUser.getFollowing().remove(targetId);
            userToModify.getFollowers().remove(freshCurrentUser.getId());
            userRepository.save(freshCurrentUser);
            userRepository.save(userToModify);

            return "User unfollowed successfully.";
        } else {
            freshCurrentUser.getFollowing().add(targetId);
            userToModify.getFollowers().add(freshCurrentUser.getId());
            userRepository.save(freshCurrentUser);
            userRepository.save(userToModify);

            Notification notification = Notification.builder()
                    .from(freshCurrentUser.getId())
                    .to(userToModify.getId())
                    .type(NotificationType.follow)
                    .build();

            Notification newNotification = notificationRepository.save(notification);

            return "User followed successfully.";
        }

    }

    public UserResponse updateUser(UserUpdateRequest request, User user) throws IOException {
        boolean updated = false;

        if (request.getFullName() != null && !request.getFullName().equals(user.getFullName())) {
            user.setFullName(request.getFullName());
            updated = true;
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            user.setUsername(request.getUsername());
            updated = true;
        }

        if (request.getBio() != null && !request.getBio().equals(user.getBio())) {
            user.setBio(request.getBio());
            updated = true;
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            user.setEmail(request.getEmail());
            updated = true;
        }

        if (request.getLink() != null && !request.getLink().equals(user.getLink())) {
            user.setLink(request.getLink());
            updated = true;
        }

        if (request.getProfileImg() != null) {
            if (user.getProfileImg() != null && !user.getProfileImg().isEmpty()) {
                String publicId = ImageUtils.extractPublicId(user.getProfileImg());
                cloudinary.uploader().destroy(publicId, Map.of());
            }
            Map<String, Object> uploadResult = cloudinary.uploader().upload(request.getProfileImg(), Map.of());
            user.setProfileImg((String) uploadResult.get("secure_url"));
            updated = true;
        }

        if (request.getCoverImg() != null) {
            if (user.getCoverImg() != null && !user.getCoverImg().isEmpty()) {
                String publicId = ImageUtils.extractPublicId(user.getCoverImg());
                cloudinary.uploader().destroy(publicId, Map.of());
            }
            Map<String, Object> uploadResult = cloudinary.uploader().upload(request.getCoverImg(), Map.of());
            user.setCoverImg((String) uploadResult.get("secure_url"));
            updated = true;
        }

        if (updated) {
            userRepository.save(user);
        }

        return new UserResponse(user);

    }

    public User updatePassword(String userId, String currentPassword, String newPassword) {
        if (currentPassword == null || newPassword == null || currentPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide both current and new passwords.");
        }

        User user = userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        boolean isMatch = passwordEncoder.matches(currentPassword, user.getPassword());

        if (!isMatch) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password provided.");
        }

        if (newPassword.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters long.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }


}
