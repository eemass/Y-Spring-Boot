package com.samiul.Y.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data

public class NotificationResponse {
    @JsonProperty("_id")
    private String id;

    private String type;
    private boolean read;
    private Instant createdAt;

    private FromUser from;

    @Data
    public static class FromUser {
        private String username;
        private String profileImg;
    }
}
