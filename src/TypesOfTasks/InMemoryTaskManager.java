package TypesOfTasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Epic> epics = new ArrayList<>();
    private final List<Subtask> subtasks = new ArrayList<>();
    private final List<Task> history = new ArrayList<>();

    @Override
    public Task createTask(String title, String description) {
        Task task = new Task(generateId(), title, description);
        tasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(generateId(), title, description);
        epics.add(epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(String title, String description, int epicId) {
        Subtask subtask = new Subtask(generateId(), title, description, epicId);
        subtasks.add(subtask);
        Epic epic = getEpic(epicId);
        if (epic != null) {
            epic.addSubtask(subtask.getId());
        }
        return subtask;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        if (task != null) {
            history.add(task);
        }
        return task;
    }


    @Override
    public Epic getEpic(int id) {
        return epics.stream().filter(epic -> epic.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.stream().filter(subtask -> subtask.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        // Находим и обновляем подзадачу в списке
        int index = subtasks.indexOf(subtask);
        if (index != -1) {
            subtasks.set(index, subtask);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks);
        allTasks.addAll(epics);
        allTasks.addAll(subtasks);
        return allTasks;
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return new ArrayList<>(epics);
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks);
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            if (subtask.getEpicId() == epicId) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public void deleteEpicById(int id) {
        epics.removeIf(epic -> epic.getId() == id);
        subtasks.removeIf(subtask -> subtask.getEpicId() == id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        subtasks.removeIf(subtask -> subtask.getId() == id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    private int generateId() {
        return tasks.size() + epics.size() + subtasks.size() + 1;
    }
}
