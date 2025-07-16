package com.samiul.Y.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @JsonProperty("_id")
    private ObjectId id;

    private String username;
    private String fullName;
    private String email;
    private String password;

    private String profileImg = "";
    private String coverImg = "";
    private String bio = "";
    private String link = "";

    private List<ObjectId> followers = new ArrayList<>();
    private List<ObjectId> following = new ArrayList<>();
    private List<ObjectId> likedPosts = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
