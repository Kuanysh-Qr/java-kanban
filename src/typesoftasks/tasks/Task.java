package typesoftasks.tasks;

import java.util.Objects;

public class Task {
    private final int id;
    private final String title;
    private final String description;
    private TaskStatus status;
    private final TaskType type;

    public Task(int id, String title, String description, TaskType type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = type;
    }

    public Task(int id, String title, String description) {
        this(id, title, description, TaskType.TASK); // по умолчанию TASK
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

     @Override
    public String toString() {
        return "Task{" + "id=" + id + ", title='" + title + '\'' + ", " +
                "description='" + description + '\'' + "," +
                "status=" + status + ", type=" + type + '}';
    }
}