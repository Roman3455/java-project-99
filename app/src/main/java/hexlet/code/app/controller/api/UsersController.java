package hexlet.code.app.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
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

import hexlet.code.app.dto.user.UserDTO;
import hexlet.code.app.dto.user.UserCreateDTO;
import hexlet.code.app.dto.user.UserUpdateDTO;
import hexlet.code.app.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> index() {
        var result = userService.getAllUsers();
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

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
    public UserDTO show(@PathVariable long id) {

        return userService.getUserById(id);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("#id == @userUtils.getCurrentUser().getId()")
    public UserDTO update(@PathVariable long id, @Valid @RequestBody UserUpdateDTO dto) {

        return userService.updateUser(id, dto);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("#id == @userUtils.getCurrentUser().getId()")
    public void destroy(@PathVariable long id) {

        userService.deleteUser(id);
    }
}
