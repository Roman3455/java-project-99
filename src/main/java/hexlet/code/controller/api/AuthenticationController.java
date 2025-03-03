package hexlet.code.controller.api;

import hexlet.code.dto.authentication.AuthenticationRequest;
import hexlet.code.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public String createToken(@RequestBody AuthenticationRequest authRequest) {
        var response = authenticationService.createAuthToken(authRequest);

        return response.getToken();
    }
}
