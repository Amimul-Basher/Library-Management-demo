package com.amimul.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RegistrationRequest {

    @NotEmpty(message="Firstname is required")
    @NotBlank(message = "Firstname is mandatory")
    private String firstname;
    @NotEmpty(message="Lastname is required")
    @NotBlank(message = "Lastname is mandatory")
    private String lastname;
    @Email(message = "Must be a valid email address")
    @NotEmpty(message="Email is required")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotEmpty(message="Password is required")
    @NotBlank(message = "Password is mandatory")
    @Size(min=8, message = "Password should least be 8 Characters long")
    private String password;


}
