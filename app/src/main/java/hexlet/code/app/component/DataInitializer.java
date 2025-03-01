package hexlet.code.app.component;

import hexlet.code.app.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.app.model.entity.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.TaskStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TaskStatusService taskStatusService;

    @Override
    public void run(ApplicationArguments args) {
        var data = new User();
        data.setEmail("hexlet@example.com");
        data.setPassword(passwordEncoder.encode("qwerty"));

        userRepository.save(data);

        var statuses = List.of(
                new String[]{"draft", "draft"},
                new String[]{"to review", "to_review"},
                new String[]{"to be fixed", "to_be_fixed"},
                new String[]{"to publish", "to_publish"},
                new String[]{"published", "published"}
        );

        statuses.forEach(status -> {
            var dto = new TaskStatusCreateDTO();
            dto.setName(status[0]);
            dto.setSlug(status[1]);
            taskStatusService.createTaskStatus(dto);
        });
    }
}
