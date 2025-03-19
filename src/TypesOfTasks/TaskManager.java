package TypesOfTasks;

import java.util.*;

public class TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int generateId() {
        return idCounter++;
    }

    public Task createTask(String title, String description) {
        Task task = new Task(generateId(), title, description);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(generateId(), title, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(String title, String description, int epicId) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден");
        }
        Subtask subtask = new Subtask(generateId(), title, description, epicId);
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).addSubtask(subtask.getId());
        epics.get(epicId).updateStatus(this);
        return subtask;
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Подзадача с id " + subtask.getId() + " не найдена");
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.updateStatus(this);
        }
    }

    public Collection<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    public List<Subtask> getSubtasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик с id " + epicId + " не найден");
        }
        List<Subtask> subtaskList = new ArrayList<>();
        for (int subtaskId : epics.get(epicId).getSubtasks()) {
            subtaskList.add(subtasks.get(subtaskId));
        }
        return subtaskList;
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        for (int subtaskId : epics.get(id).getSubtasks()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).removeSubtask(id);
        subtasks.remove(id);
        epics.get(epicId).updateStatus(this);
    }
}
