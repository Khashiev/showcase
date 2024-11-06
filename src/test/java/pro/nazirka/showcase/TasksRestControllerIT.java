package pro.nazirka.showcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskRepositoryImpl taskRepository;

    @AfterEach
    void tearDown() {
        taskRepository.getTasks().clear();
    }

    @Test
    void getTasksReturnsValidResponseEntity() throws Exception {
        var requestBuilder = get("/api/tasks");
        this.taskRepository.getTasks()
                .addAll(List.of(
                        new Task(UUID.fromString("7cbd4a86-9c5d-11ef-a96a-13456a082682"),
                                "first", false),
                        new Task(UUID.fromString("84263cc4-9c5d-11ef-bc48-c7ecbc94d7a6"),
                                "second", true)));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                      "id": "7cbd4a86-9c5d-11ef-a96a-13456a082682",
                                      "details": "first",
                                      "completed": false
                                    },
                                    {
                                      "id": "84263cc4-9c5d-11ef-bc48-c7ecbc94d7a6",
                                      "details": "second",
                                      "completed": true
                                    }
                                ]
                                """)
                );
    }

    @Test
    void createTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "details": "third"
                        }
                        """);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                   "details": "third",
                                   "completed": false
                                }
                                """),
                        jsonPath("$.id").exists()
                );
        final var task = this.taskRepository.getTasks().get(0);
        assertEquals(1, this.taskRepository.getTasks().size());
        assertNotNull(task.id());
        assertEquals("third", this.taskRepository.getTasks().get(0).details());
        assertFalse(this.taskRepository.getTasks().get(0).completed());
    }

    @Test
    void createTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "details": null
                        }
                        """);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                   "errors": ["Task details must be set"]
                                }
                                """, true)
                );
        assertTrue(this.taskRepository.getTasks().isEmpty());
    }
}