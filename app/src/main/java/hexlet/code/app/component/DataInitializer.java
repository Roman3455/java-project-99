package hexlet.code.app.component;

import hexlet.code.app.dto.label.LabelCreateDTO;
import hexlet.code.app.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.app.dto.user.UserCreateDTO;
import hexlet.code.app.service.LabelService;
import hexlet.code.app.service.TaskStatusService;
import hexlet.code.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    private final LabelService labelService;

    @Override
    public void run(ApplicationArguments args) {

        var userData = new UserCreateDTO();
        userData.setEmail("hexlet@example.com");
        userData.setPassword("qwerty");
        userService.createUser(userData);

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

        var labels = List.of("bug", "feature");
        labels.forEach(label -> {
            var dto = new LabelCreateDTO();
            dto.setName(label);
            labelService.createLabel(dto);
        });
    }
}
