package hexlet.code.service;

import hexlet.code.dto.authentication.AuthenticationRequest;
import hexlet.code.dto.authentication.AuthenticationResponse;
import hexlet.code.exception.AuthenticationException;
import hexlet.code.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;
    private final JWTUtils jwtUtils;

    public AuthenticationResponse createAuthToken(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    ));
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Login or password is incorrect");
        }

        var user = customUserDetailService.loadUserByUsername(authenticationRequest.getUsername());
        String token = jwtUtils.generateToken(user);

        return new AuthenticationResponse(token);
    }
}
