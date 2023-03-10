package io.github.dankoller.springrecipe.request;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * This class represents a request to register a new user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @Email(regexp = ".+@.+\\..+", message = "Email is not valid")
    private String email;
    @Length(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
