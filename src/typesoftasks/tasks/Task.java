package typesoftasks.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final int id;
    private final String title;
    private final String description;
    private TaskStatus status;
    private final TaskType type;
    private Duration duration;
    private LocalDateTime startTime;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

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

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }
    }

    public static Task fromString(String line) {
        String[] fields = line.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        long durationMinutes = Long.parseLong(fields[5]);
        String startTimeStr = fields[6];

        Task task = new Task(id, title, description, type);
        task.setStatus(status);
        task.setDuration(Duration.ofMinutes(durationMinutes));

        if (!startTimeStr.isEmpty()) {
            task.setStartTime(LocalDateTime.parse(startTimeStr));
        }

        return task;
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
        return id + "," +
                type + "," +
                title + "," +
                status + "," +
                description + "," +
                (duration != null ? duration.toMinutes() : 0) + "," +
                (startTime != null ? startTime : "") + "," +
                (getEndTime() != null ? getEndTime() : "");
    }

}
