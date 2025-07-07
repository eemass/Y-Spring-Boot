package com.samiul.Y.dto;

import jakarta.validation.constraints.NotBlank;

public class SignupRequest {
    @NotBlank
    private String username;
    private String fullName;
    private String email;
    private String password;

}
