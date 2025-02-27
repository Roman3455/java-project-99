package hexlet.code.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.user.UserDTO;
import hexlet.code.app.dto.user.UserUpdateDTO;
import hexlet.code.app.model.entity.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
final class UsersControllerTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        mvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModelWithAllFields()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    void testIndex() throws Exception {
        var request = mvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", String.valueOf(userRepository.count())))
                .andReturn()
                .getResponse();
        var responseBody = request.getContentAsString();

        var actual = om.readValue(responseBody, new TypeReference<List<UserDTO>>() {
        });
        var expected = userRepository.findAll();

        assertThatJson(responseBody).isArray()
                .isNotEmpty()
                .hasSize(expected.size());

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("password", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void testIndexWithoutUsers() throws Exception {
        userRepository.deleteAll();

        mvc.perform(get("/api/users").with(token))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void testShow() throws Exception {
        var request = mvc.perform(get("/api/users/" + testUser.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = request.getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("createdAt").isEqualTo(testUser.getCreatedAt()));

        assertThatJson(body).isObject()
                .doesNotContainValue("password");
    }

    @Test
    void testShowWithInvalidId() throws Exception {
        mvc.perform(get("/api/users/" + 999).with(token))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with id 999 not found"));
    }

    @Test
    void testCreateWithAllFields() throws Exception {
        var userData = Instancio.of(modelGenerator.getUserModelWithAllFields()).create();

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        var user = userRepository.findByEmail(userData.getEmail()).orElse(null);

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(userData.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userData.getLastName());
        assertThat(user.getEmail()).isEqualTo(userData.getEmail());
        assertThat(user.getCreatedAt()).isEqualTo(LocalDate.now());
        assertTrue(passwordEncoder.matches(userData.getPassword(), user.getPassword()));
    }

    @Test
    void testCreateWithRequiredFields() throws Exception {
        var userData = Instancio.of(modelGenerator.getUserModelWithRequiredFields()).create();

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        var user = userRepository.findByEmail(userData.getEmail()).orElse(null);

        assertNotNull(user);
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertThat(user.getEmail()).isEqualTo(userData.getEmail());
        assertThat(user.getCreatedAt()).isEqualTo(LocalDate.now());
        assertTrue(passwordEncoder.matches(userData.getPassword(), user.getPassword()));
    }

    @Test
    void testCreateWithInvalidParameters() throws Exception {
        var userData = Instancio.of(modelGenerator.getUserModelWithRequiredFields()).create();
        userData.setEmail("invalidemail,com");
        userData.setPassword("11");

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.password").value("Password must be at least 3 characters long"))
                .andExpect(jsonPath("$.email").value("Email must be valid"));
    }

    @Test
    void testCreateWithNullParameters() throws Exception {
        var userData = Instancio.of(modelGenerator.getUserModelWithRequiredFields()).create();
        userData.setEmail(null);
        userData.setPassword(null);

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(userData));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.password").value("Password is required"))
                .andExpect(jsonPath("$.email").value("Email is required"));
    }

    @Test
    void testUpdateWithAllFields() throws Exception {
        var data = new HashMap<String, String>();
        data.put("firstName", "John");
        data.put("lastName", "Doe");
        data.put("email", "john.doe@example.com");
        data.put("password", "123");

        var request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mvc.perform(request)
                .andExpect(status().isOk());

        var user = userRepository.findById(testUser.getId()).orElse(null);

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(data.get("firstName"));
        assertThat(user.getLastName()).isEqualTo(data.get("lastName"));
        assertThat(user.getEmail()).isEqualTo(data.get("email"));
        assertTrue(passwordEncoder.matches(data.get("password"), user.getPassword()));
        assertThat(user.getUpdatedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    void testUpdateWithoutRequiredFields() throws Exception {
        var userBeforeUpdate = userRepository.findById(testUser.getId()).orElse(null);

        var data = new HashMap<String, String>();
        data.put("firstName", "John");
        data.put("lastName", "Doe");

        var request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mvc.perform(request)
                .andExpect(status().isOk());

        var userAfterUpdate = userRepository.findById(testUser.getId()).orElse(null);

        assertNotNull(userAfterUpdate);
        assertThat(userAfterUpdate.getFirstName()).isEqualTo(data.get("firstName"));
        assertThat(userAfterUpdate.getLastName()).isEqualTo(data.get("lastName"));
        assertThat(userBeforeUpdate.getEmail()).isEqualTo(userAfterUpdate.getEmail());
        assertEquals(userBeforeUpdate.getPassword(), userAfterUpdate.getPassword());
    }

    @Test
    void testUpdateWithInvalidFields() throws Exception {
        var data = new HashMap<String, String>();
        data.put("email", "invalidemail,com");
        data.put("password", "11");

        var request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.password").value("Password must be at least 3 characters long"))
                .andExpect(jsonPath("$.email").value("Email must be valid"));
    }

    @Test
    void testUpdateWithNullFields() throws Exception {
        var data = new UserUpdateDTO();
        data.setFirstName(JsonNullable.of(null));
        data.setLastName(JsonNullable.of(null));
        data.setEmail(JsonNullable.of(null));
        data.setPassword(JsonNullable.of(null));

        var request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.password").value("Password is required"))
                .andExpect(jsonPath("$.email").value("Email is required"));
    }

    @Test
    void testUpdateWithInvalidId() throws Exception {
        var data = new HashMap<String, String>();
        data.put("email", "test@example.com");
        data.put("password", "password");

        mvc.perform(put("/api/users/999")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(data)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDestroy() throws Exception {
        mvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/users/" + testUser.getId())
                        .with(token))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(testUser.getId()));
    }

    @Test
    void testDestroyWithInvalidId() throws Exception {
        mvc.perform(delete("/api/users/999")
                        .with(token))
                .andExpect(status().isForbidden());
    }

    @AfterAll
    static void tearDown(@Autowired UserRepository userRepository) {
        userRepository.deleteAll();
    }
}
