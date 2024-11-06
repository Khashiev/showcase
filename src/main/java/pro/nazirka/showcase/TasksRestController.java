package pro.nazirka.showcase;


import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("api/tasks")
public class TasksRestController {
    private final TaskRepository taskRepository;
    private final MessageSource messageSource;

    public TasksRestController(TaskRepository taskRepository,
                               MessageSource messageSource) {
        this.taskRepository = taskRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createTask(
            @RequestBody NewTaskPayload payload,
            UriComponentsBuilder uriBuilder,
            Locale locale) {
        if (payload.details() == null || payload.details().isBlank()) {
            final var message = this.messageSource
                    .getMessage("tasks.create.details.errors.not_set",
                            new Object[]{}, locale);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorsPresentation(
                            List.of(message)));
        } else {

            var task = new Task(payload.details());
            this.taskRepository.save(task);

            return ResponseEntity.created(uriBuilder
                            .path("api/tasks/{taskId}")
                            .build(Map.of("taskId", task.id())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(task);
        }
    }
}