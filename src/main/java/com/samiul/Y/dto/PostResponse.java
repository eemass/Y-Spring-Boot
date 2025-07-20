package com.samiul.Y.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
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
    public static class CommentResponse {
        private String text;
        private SimpleUser user;
    }

    @Data
    public static class SimpleUser {
        @JsonProperty("_id")
        private String id;

        private String username;
        private String fullName;
        private String profileImg;
    }
}
