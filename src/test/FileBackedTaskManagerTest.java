package test;

import org.junit.jupiter.api.Test;
import typesoftasks.managers.FileBackedTaskManager;
import typesoftasks.tasks.Epic;
import typesoftasks.tasks.Subtask;
import typesoftasks.tasks.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void saveAndLoadEmptyManager() throws Exception {
        File file = File.createTempFile("test-empty-manager", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getAllTasks().isEmpty(), "Tasks should be empty");
        assertTrue(loaded.getAllEpics().isEmpty(), "Epics should be empty");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Subtasks should be empty");
    }

    @Test
    void saveAndLoadMultipleTasks() throws Exception {
        File file = File.createTempFile("test-multiple-tasks", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task1 = manager.createTask("Task1", "Desc1");
        Task task2 = manager.createTask("Task2", "Desc2");
        Epic epic1 = manager.createEpic("Epic1", "EpicDesc1");
        Subtask sub1 = manager.createSubtask("Subtask1", "SubDesc1", epic1.getId());
        Subtask sub2 = manager.createSubtask("Subtask2", "SubDesc2", epic1.getId());
        Epic epic2 = manager.createEpic("Epic2", "EpicDesc2");

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loaded.getAllTasks().stream()
                .filter(t -> !(t instanceof Epic) && !(t instanceof Subtask)).count(), "There should be 2 tasks");
        assertEquals(2, loaded.getAllEpics().size(), "There should be 2 epics");
        assertEquals(2, loaded.getAllSubtasks().size(), "There should be 2 subtasks");

        assertTrue(loaded.getAllTasks().stream()
                .anyMatch(t -> t.getTitle().equals("Task1") && t.getDescription().equals("Desc1")));
        assertTrue(loaded.getAllEpics().stream()
                .anyMatch(e -> e.getTitle().equals("Epic1") && e.getDescription().equals("EpicDesc1")));
        assertTrue(loaded.getAllSubtasks().stream()
                .anyMatch(s -> s.getTitle().equals("Subtask1") && s.getDescription().equals("SubDesc1")));
    }

    @Test
    void saveAndLoadTasksContent() throws Exception {
        File file = File.createTempFile("test-tasks-content", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = manager.createTask("Task42", "Desc42");
        Epic epic = manager.createEpic("Epic42", "EpicDesc42");
        Subtask subtask = manager.createSubtask("Sub42", "SubDesc42", epic.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        Task loadedTask = loaded.getAllTasks().stream()
                .filter(t -> t.getTitle().equals("Task42")).findFirst().orElse(null);
        assertNotNull(loadedTask);
        assertEquals("Desc42", loadedTask.getDescription());

        Epic loadedEpic = loaded.getAllEpics().stream()
                .filter(e -> e.getTitle().equals("Epic42")).findFirst().orElse(null);
        assertNotNull(loadedEpic);
        assertEquals("EpicDesc42", loadedEpic.getDescription());

        Subtask loadedSubtask = loaded.getAllSubtasks().stream()
                .filter(s -> s.getTitle().equals("Sub42")).findFirst().orElse(null);
        assertNotNull(loadedSubtask);
        assertEquals("SubDesc42", loadedSubtask.getDescription());
        assertEquals(loadedEpic.getId(), loadedSubtask.getEpicId());
    }

    @Test
    void shouldThrowIfTasksIntersect() throws Exception {
        File file = File.createTempFile("test-intersect", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task t1 = manager.createTask("T1", "desc");
        t1.setStartTime(LocalDateTime.of(2025, 6, 10, 10, 0));
        t1.setDuration(Duration.ofMinutes(60));
        manager.updateTask(t1);

        Task t2 = manager.createTask("T2", "desc");
        t2.setStartTime(LocalDateTime.of(2025, 6, 10, 10, 30)); // пересекается
        t2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> manager.updateTask(t2));
    }

    @Test
    void shouldThrowIfFileIsUnreadable() {
        File file = new File("nonexistent/path/does-not-exist.csv");
        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void shouldRestoreHistory() throws Exception {
        File file = File.createTempFile("test-history", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task t1 = manager.createTask("T1", "desc");
        manager.getTask(t1.getId()); // добавляем в историю

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        List<Task> history = loaded.getHistory();
        assertEquals(1, history.size());
        assertEquals(t1.getId(), history.get(0).getId());
    }
}
