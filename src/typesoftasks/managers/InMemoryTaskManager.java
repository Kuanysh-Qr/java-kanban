package typesoftasks.managers;

import typesoftasks.tasks.Epic;
import typesoftasks.tasks.Subtask;
import typesoftasks.tasks.Task;
import typesoftasks.tasks.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = new InMemoryHistoryManager();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );

    private int currentId = 1;

    private int generateId() {
        return currentId++;
    }

    protected Map<Integer, Task> getTaskMap() {
        return tasks;
    }

    protected Map<Integer, Epic> getEpicMap() {
        return epics;
    }

    protected Map<Integer, Subtask> getSubtaskMap() {
        return subtasks;
    }

    private boolean isTimeIntersecting(Task t1, Task t2) {
        return !(t1.getEndTime().isBefore(t2.getStartTime()) || t1.getStartTime().isAfter(t2.getEndTime()));
    }

    private boolean hasIntersection(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }

        for (Task existing : getPrioritizedTasks()) {
            if (existing.getId() == newTask.getId()) continue;

            if (existing.getStartTime() == null || existing.getEndTime() == null) continue;

            if (existing.getStartTime().isAfter(newTask.getEndTime())) {
                break;
            }

            if (isTimeIntersecting(newTask, existing)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task createTask(String title, String description) {
        Task task = new Task(generateId(), title, description);

        if (task.getStartTime() != null && hasIntersection(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(generateId(), title, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(String title, String description, int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return null;

        Subtask subtask = new Subtask(generateId(), title, description, epicId);
        if (subtask.getStartTime() != null && hasIntersection(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени.");
        }

        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        return subtask;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());
        if (oldTask != null && oldTask.getStartTime() != null) {
            prioritizedTasks.remove(oldTask);
        }

        if (task.getStartTime() != null && hasIntersection(task)) {
            throw new IllegalArgumentException("Обновлённая задача пересекается по времени.");
        }

        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask old = subtasks.get(subtask.getId());
        if (old != null && old.getStartTime() != null) {
            prioritizedTasks.remove(old);
        }

        if (subtask.getStartTime() != null && hasIntersection(subtask)) {
            throw new IllegalArgumentException("Обновлённая подзадача пересекается по времени.");
        }

        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> all = new ArrayList<>();
        all.addAll(tasks.values());
        all.addAll(epics.values());
        all.addAll(subtasks.values());
        return all;
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return List.of();

        return epic.getSubtasks().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null && subtask.getStartTime() != null) {
                    prioritizedTasks.remove(subtask);
                }
                historyManager.remove(subtaskId);
            }
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null && subtask.getStartTime() != null) {
            prioritizedTasks.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
            }
        }
        historyManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtasks();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask == null) continue;

            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
