package pro.nazirka.showcase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TasksRestControllerTest {
    @Mock
    TaskRepository taskRepository;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    TasksRestController controller;

    @Test
    void getTasksReturnsValidResponseEntity() {
        var tasks = List.of(new Task(UUID.randomUUID(), "first", false),
                new Task(UUID.randomUUID(), "second", true));
        Mockito.doReturn(tasks).when(taskRepository).findAll();

        var responseEntity = this.controller.getTasks();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(tasks, responseEntity.getBody());
    }

    @Test
    void createTask_PayloadIsValid_ReturnsValidResponseEntity() {
        var details = "third";

        var responseEntity = this.controller.createTask(new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"),
                Locale.ENGLISH);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());

        if (responseEntity.getBody() instanceof Task task) {
            assertNotNull(task.id());
            assertEquals(details, task.details());
            assertFalse(task.completed());
            assertEquals(URI.create("http://localhost:8080/api/tasks/" + task.id()),
                    responseEntity.getHeaders().getLocation());
            Mockito.verify(this.taskRepository).save(task);
        } else {
            assertInstanceOf(Task.class, responseEntity.getBody());
        }

        Mockito.verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void createTask_PayloadIsInvalid_ReturnsValidResponseEntity() {
        var details = "    ";
        var locale = Locale.US;
        var errorMessage = "Details is Empty";

        Mockito.doReturn(errorMessage).when(this.messageSource)
                .getMessage("tasks.create.details.errors.not_set",
                        new Object[]{}, locale);

        var responseEntity = this.controller.createTask(new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), locale);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorsPresentation(List.of(errorMessage)), responseEntity.getBody());

        Mockito.verifyNoInteractions(this.taskRepository);
    }

    @Test
    void getTask_ReturnsValidResponseEntity() {
        var task = new Task(UUID.randomUUID(), "first", false);
        Mockito.doReturn(Optional.of(task)).when(taskRepository).findById(task.id());

        var responseEntity = this.controller.getTask(task.id());

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(task, responseEntity.getBody());
    }
}