package com.userservice.model;

import com.userservice.validator.ValidEmail;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull
    @NotEmpty(message = "First name can not be empty")
    private String firstName;
    
    @NotNull
    @NotEmpty(message = "Last name can not be empty")
    private String lastName;

    @NotNull
    @NotEmpty(message = "Password name can not be empty")
    private String password;

    @ValidEmail
    @NotNull
    @NotEmpty(message = "Email name can not be empty")
    private String email;

}