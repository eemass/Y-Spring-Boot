package com.samiul.Y.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samiul.Y.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserResponse {
    @JsonProperty("_id")
    private String id;

    private String fullName;
    private String username;
    private String email;
    private String profileImg;
    private String coverImg;
    private List<String> followers;
    private List<String> following;

    public UserResponse(User user) {
        this.id = user.getId().toHexString(); // ObjectId → String
        this.fullName = user.getFullName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profileImg = user.getProfileImg();
        this.coverImg = user.getCoverImg();
        this.followers = user.getFollowers()
                .stream()
                .map(ObjectId::toHexString)
                .collect(Collectors.toList());

        this.following = user.getFollowing()
                .stream()
                .map(ObjectId::toHexString)
                .collect(Collectors.toList());
    }

}
