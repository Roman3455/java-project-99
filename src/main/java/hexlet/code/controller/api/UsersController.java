package hexlet.code.controller.api;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

    private static final String PRINCIPAL = """
        @userUtils.getCurrentUser().getId() == #id
    """;

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> index() {
        var result = userService.getAllUsers();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO dto) {

        return userService.createUser(dto);
    }

    @GetMapping(path = "/{id}")
    public UserDTO show(@PathVariable Long id) {

        return userService.getUserById(id);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize(PRINCIPAL)
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {

        return userService.updateUser(id, dto);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(PRINCIPAL)
    public void destroy(@PathVariable Long id) {

        userService.deleteUser(id);
    }
}
