package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.entity.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.util.ModelGenerator;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private TaskStatus taskStatus;

    @BeforeEach
    public void setUp() {
        taskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(taskStatus);
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @AfterEach
    void tearDown() {
        taskStatusRepository.deleteAll();
    }

    @Test
    void testIndex() throws Exception {
        var request = mvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", String.valueOf(taskStatusRepository.count())))
                .andReturn()
                .getResponse();

        var responseBody = request.getContentAsString();
        assertThatJson(responseBody).isArray().isNotEmpty();
    }

    @Test
    void testShow() throws Exception {
        var request = mvc.perform(get("/api/task_statuses/" + taskStatus.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var responseBody = request.getContentAsString();
        assertThatJson(responseBody).and(
                a -> a.node("name").isEqualTo(taskStatus.getName()),
                a -> a.node("slug").isEqualTo(taskStatus.getSlug())
        );
    }

    @Test
    void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        var request = mvc.perform(post("/api/task_statuses").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        var responseBody = request.getContentAsString();

        assertThatJson(responseBody).and(
                v -> v.node("id").isPresent(),
                v -> v.node("name").isEqualTo(data.getName()),
                v -> v.node("slug").isEqualTo(data.getSlug())
        );
    }

    @Test
    void testUpdate() throws Exception {
        var data = Map.of("name", "test");
        var request = mvc.perform(put("/api/task_statuses/" + taskStatus.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isOk());

        var updatedStatus = taskStatusRepository.findBySlug(taskStatus.getSlug()).get();
        assertThat(updatedStatus).isNotNull();
        assertThat(updatedStatus.getName()).isEqualTo(data.get("name"));
        assertThat(updatedStatus.getSlug()).isEqualTo(taskStatus.getSlug());
    }

    @Test
    void testDestroy() throws Exception {
        mvc.perform(delete("/api/task_statuses/" + taskStatus.getId()).with(token))
                .andExpect(status().isNoContent());

        assertFalse(taskStatusRepository.existsById(taskStatus.getId()));
    }
}
