package hexlet.code.app.service;

import hexlet.code.app.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userService.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", email)));
        var customUserDetails = new CustomUserDetails(user);

        return new org.springframework.security.core.userdetails.User(
                customUserDetails.getUsername(),
                customUserDetails.getPassword(),
                customUserDetails.getAuthorities()
        );
    }
}
