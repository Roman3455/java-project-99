package hexlet.code.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email is required")
    private JsonNullable<String> email;

    @NotNull(message = "Password is required")
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private JsonNullable<String> password;
}
