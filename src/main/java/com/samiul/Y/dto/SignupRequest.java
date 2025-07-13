package com.samiul.Y.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "Username is required.")
    private String username;

    @NotBlank(message = "Full name is required.")
    private String fullName;

    @NotBlank
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(message = "Password length must be at least 6 characters.")
    private String password;

}
