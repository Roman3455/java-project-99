package hexlet.code.app.util;

import hexlet.code.app.model.entity.TaskStatus;
import hexlet.code.app.model.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class ModelGenerator {

    private Model<User> userModelWithAllFields;
    private Model<User> userModelWithRequiredFields;
    private Model<TaskStatus> taskStatusModel;

    private final Faker faker;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {

        userModelWithAllFields = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> passwordEncoder.encode(faker.internet().password()))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .toModel();

        userModelWithRequiredFields = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getFirstName))
                .ignore(Select.field(User::getLastName))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> passwordEncoder.encode(faker.internet().password()))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.word().noun())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.word().noun())
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .toModel();
    }
}
