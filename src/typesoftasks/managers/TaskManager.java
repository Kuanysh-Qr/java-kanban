public interface TaskManager {
    Task createTask(String title, String description);

    Epic createEpic(String title, String description);

    Subtask createSubtask(String title, String description, int epicId);

    Subtask getSubtaskById(int id);

    Task getTask(int id);

    Epic getEpic(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getAllTasks();

    Collection<Epic> getAllEpics();

    Collection<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksByEpic(int epicId);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<Task> getHistory();

    void updateEpicStatus(Epic epic);
}
