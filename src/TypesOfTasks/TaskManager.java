package TypesOfTasks;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Task createTask(String title, String description);
    Epic createEpic(String title, String description);
    Subtask createSubtask(String title, String description, int epicId);

    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtaskById(int id);

    void updateSubtask(Subtask subtask);

    List<Task> getAllTasks();
    Collection<Epic> getAllEpics();
    Collection<Subtask> getAllSubtasks();
    List<Subtask> getSubtasksByEpic(int epicId);

    void deleteTaskById(int id);
    void deleteEpicById(int id);
    void deleteSubtaskById(int id);

    List<Task> getHistory();
}
