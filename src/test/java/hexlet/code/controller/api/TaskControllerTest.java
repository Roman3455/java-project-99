package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Task testTask;

    private TaskStatus testTaskStatus;

    @BeforeEach
    void setup() {
        var testUser = Instancio.of(modelGenerator.getUserModelWithRequiredFields()).create();
        userRepository.save(testUser);

        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        taskRepository.save(testTask);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testIndex() throws Exception {
        var request = mvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var responseBody = request.getContentAsString();
        assertThatJson(responseBody).isArray();
    }

    @Test
    void testShow() throws Exception {
        var request = mvc.perform(get("/api/tasks/" + testTask.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var responseBody = request.getContentAsString();
        assertThatJson(responseBody).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("taskLabelIds").isArray()
        );
    }

    @Test
    void testCreate() throws Exception {
        var data = Map.of(
                "title", "Test Title",
                "content", "Test Content",
                "status", testTaskStatus.getSlug());

        var request = mvc.perform(post("/api/tasks").with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated());

        var responseBody = taskRepository.findByName(data.get("title")).orElseThrow();
        assertNotNull(responseBody);
        assertThat(responseBody.getName()).isEqualTo(data.get("title"));
        assertThat(responseBody.getDescription()).isEqualTo(data.get("content"));
        assertThat(responseBody.getTaskStatus().getSlug()).isEqualTo(testTaskStatus.getSlug());
        assertThat(responseBody.getCreatedAt()).isNotNull();
    }

    @Test
    void testUpdate() throws Exception {
        var data = Map.of(
                "title", "Test Title",
                "content", "Test Content",
                "status", testTaskStatus.getSlug());

        var request = mvc.perform(put("/api/tasks/" + testTask.getId()).with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk());


        var responseBody = taskRepository.findByName(data.get("title")).orElseThrow();
        assertThat(responseBody.getName()).isEqualTo(data.get("title"));
        assertThat(responseBody.getDescription()).isEqualTo(data.get("content"));
        assertThat(responseBody.getTaskStatus().getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    void testDestroy() throws Exception {
        mvc.perform(delete("/api/tasks/" + testTask.getId()).with(token))
                .andExpect(status().isNoContent());
        assertFalse(taskRepository.findById(testTask.getId()).isPresent());
    }

    @Test
    void testTaskParams() throws Exception {
        var url = "/api/tasks?titleCont=create&taskStatus=draft&labelId=1";
        mvc.perform(get(url).with(token))
                .andExpect(status().isOk());
    }
}
