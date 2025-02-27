package hexlet.code.app.component;

import hexlet.code.app.model.entity.User;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        var data = new User();
        data.setEmail("hexlet@example.com");
        data.setPassword(passwordEncoder.encode("qwerty"));

        userRepository.save(data);
    }
}
