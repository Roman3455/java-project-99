package hexlet.code.util;

//import hexlet.code.model.entity.Label;
//import hexlet.code.model.entity.Task;
//import hexlet.code.model.entity.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//import java.util.HashSet;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModelWithAllFields;
    private Model<User> userModelWithRequiredFields;
//    private Model<TaskStatus> taskStatusModel;
//    private Model<Task> taskModel;
//    private Model<Label> labelModel;

    @Autowired
    private Faker faker;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {

        userModelWithAllFields = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getHashedPassword), () -> passwordEncoder
                        .encode(faker.internet().password()))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .toModel();

        userModelWithRequiredFields = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getFirstName))
                .ignore(Select.field(User::getLastName))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getHashedPassword), () -> faker.internet().password())
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .toModel();

//        taskStatusModel = Instancio.of(TaskStatus.class)
//                .ignore(Select.field(TaskStatus::getId))
//                .supply(Select.field(TaskStatus::getName), () -> faker.word().noun())
//                .supply(Select.field(TaskStatus::getSlug), () -> faker.word().noun())
//                .ignore(Select.field(TaskStatus::getCreatedAt))
//                .toModel();
//
//        taskModel = Instancio.of(Task.class)
//                .ignore(Select.field(Task::getId))
//                .ignore(Select.field(Task::getTaskStatus))
//                .ignore(Select.field(Task::getAssignee))
//                .supply(Select.field(Task::getLabels), () -> new HashSet<Label>())
//                .supply(Select.field(Task::getName), () -> faker.word().noun())
//                .supply(Select.field(Task::getDescription), () -> faker.text().text(30))
//                .supply(Select.field(Task::getIndex), () -> faker.number().positive())
//                .toModel();
//
//        labelModel = Instancio.of(Label.class)
//                .ignore(Select.field(Label::getId))
//                .supply(Select.field(Label::getName), () -> faker.text().text(3, 1000))
//                .toModel();
    }
}
