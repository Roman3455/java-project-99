package hexlet.code.controller.api;

import hexlet.code.dto.authentication.AuthenticationRequest;
import hexlet.code.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JWTUtils jwtUtils;

    @PostMapping("/login")
    public String createToken(@RequestBody AuthenticationRequest authRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword());
        authenticationManager.authenticate(authentication);

        return jwtUtils.generateToken(authRequest.getUsername());
    }
}
