package pro.nazirka.showcase;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class TaskRepositoryImpl implements TaskRepository {
    private final List<Task> tasks = new LinkedList<>() {{
        this.add(new Task("first"));
        this.add(new Task("second"));
    }};

    @Override
    public List<Task> findAll() {
        return this.tasks;
    }

    @Override
    public void save(Task task) {
        this.tasks.add(task);
    }
}
