package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private Label testLabel;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setup() {
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @AfterEach
    void tearDown() {
        labelRepository.deleteAll();
    }

    @Test
    void testIndex() throws Exception {
        var request = mvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var responseBody = request.getContentAsString();
        assertThatJson(responseBody).isArray().isNotEmpty();
    }

    @Test
    void testShow() throws Exception {
        var request = mvc.perform(get("/api/labels/" + testLabel.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var responseBody = request.getContentAsString();
        assertThatJson(responseBody).and(
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    void testCreate() throws Exception {
        var data = Map.of("name", "Test Label");
        var request = mvc.perform(post("/api/labels").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        var responseBody = request.getContentAsString();
        assertThatJson(responseBody).and(
                v -> v.node("id").isPresent(),
                v -> v.node("name").isEqualTo(data.get("name"))
        );

        var label = labelRepository.findByName(data.get("name")).orElse(null);

        assertThat(label).isNotNull();
        assertThat(label.getName()).isEqualTo("Test Label");
    }

    @Test
    void testUpdate() throws Exception {
        var data = Map.of("name", "Test Label");
        var request = mvc.perform(put("/api/labels/" + testLabel.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data)))
                .andExpect(status().isOk());

        var label = labelRepository.findById(testLabel.getId()).orElse(null);
        assertThat(label).isNotNull();
        assertThat(label.getName()).isEqualTo("Test Label");
    }

    @Test
    void testDestroy() throws Exception {
        mvc.perform(delete("/api/labels/" + testLabel.getId()).with(token))
                .andExpect(status().isNoContent());

        assertFalse(labelRepository.existsById(testLabel.getId()));
    }
}
