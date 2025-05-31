package typesoftasks.managers;

import typesoftasks.tasks.Epic;
import typesoftasks.tasks.Subtask;
import typesoftasks.tasks.Task;
import typesoftasks.tasks.TaskStatus;
import typesoftasks.tasks.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            boolean isFirstLine = true;
            for (String line : lines) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.isBlank()) {
                    break;
                }
                Task task = fromString(line);
                if (task != null) {
                    switch (task.getType()) {
                        case TASK:
                            manager.getTaskMap().put(task.getId(), task);
                            break;
                        case EPIC:
                            manager.getEpicMap().put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            manager.getSubtaskMap().put(task.getId(), (Subtask) task);
                            Epic epic = manager.getEpicMap().get(((Subtask) task).getEpicId());
                            if (epic != null) {
                                epic.addSubtask(task.getId());
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }

        return manager;
    }

    @Override
    public Task createTask(String title, String description) {
        Task task = super.createTask(title, description);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String title, String description) {
        Epic epic = super.createEpic(title, description);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(String title, String description, int epicId) {
        Subtask subtask = super.createSubtask(title, description, epicId);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    protected void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,title,status,description,epic\n");
            for (Task task : getTaskMap().values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getEpicMap().values()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtaskMap().values()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    public static String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task.getType()).append(",");
        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        }
        return sb.toString();
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(id, title, description);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(id, title, description);
                epic.setStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(id, title, description, epicId);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
