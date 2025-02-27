package hexlet.code.app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {

    private String firstName;

    private String lastName;

    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;
}
