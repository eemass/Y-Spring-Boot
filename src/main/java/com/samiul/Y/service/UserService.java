package com.samiul.Y.service;

import com.samiul.Y.dto.UserResponse;
import com.samiul.Y.model.Notification;
import com.samiul.Y.model.NotificationType;
import com.samiul.Y.model.User;
import com.samiul.Y.repository.NotificationRepository;
import com.samiul.Y.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final MongoTemplate mongoTemplate;

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
                    .type(NotificationType.FOLLOW)
                    .build();

            notificationRepository.save(notification);

            return "User followed successfully.";
        }

    }
}
