package pro.nazirka.showcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
@Sql("/sql/tasks_rest_controller/test_data.sql")
class TasksRestControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    void getTasksReturnsValidResponseEntity() throws Exception {
        var requestBuilder = get("/api/tasks");

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
    }
}