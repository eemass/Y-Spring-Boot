package com.samiul.Y.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    private String username;
    private String email;
    private String bio;
    private String link;
    private String profileImg;
    private String coverImg;
}
