package com.samiul.Y.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.samiul.Y.model.Post;
import com.samiul.Y.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class PostResponse {
    @JsonProperty("_id")
    private String id;

    private SimpleUser user;
    private String text;
    private String image;
    private List<String> likes;
    private List<CommentResponse> comments;

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @NoArgsConstructor
    public static class CommentResponse {
        private String text;
        private SimpleUser user;

        public CommentResponse(Post.Comment comment, User user) {
            this.text = comment.getText();
            this.user = new SimpleUser(user);
        }
    }

    @Data
    @NoArgsConstructor
    public static class SimpleUser {
        @JsonProperty("_id")
        private String id;

        private String username;
        private String fullName;
        private String profileImg;

        public SimpleUser(User user) {
            this.id = user.getId().toHexString();
            this.username = user.getUsername();
            this.fullName = user.getFullName();
            this.profileImg = user.getProfileImg();
        }
    }

    public PostResponse(Post post, SimpleUser user, List<CommentResponse> comments) {
        this.id = post.getId().toHexString();
        this.user = user;
        this.text = post.getText();
        this.image = post.getImage();
        this.likes = post.getLikes().stream().map(ObjectId::toHexString).toList();
        this.comments = comments;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
