package com.samiul.Y.dto;

import lombok.Getter;

@Getter
public class PasswordUpdateRequest {
    private String currentPassword;
    private String newPassword;
}
