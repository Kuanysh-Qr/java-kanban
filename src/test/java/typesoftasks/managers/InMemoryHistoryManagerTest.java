package test.java.typesoftasks.managers;

import typesoftasks.managers.InMemoryHistoryManager;
import typesoftasks.tasks.Epic;
import typesoftasks.managers.InMemoryTaskManager;
import typesoftasks.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void testEpicCannotBeItsOwnSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(epic.getId());
        });

        assertEquals("Epic cannot be its own subtask", exception.getMessage(), "Сообщение об ошибке не совпадает.");
    }

    @Test
    void shouldAddTaskToHistory() {

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task(1,"Task 1", "Description 1"); // 1 — id
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void shouldNotContainDuplicatesInHistory() {

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1,"Task 1", "Description 1");

        historyManager.add(task);
        historyManager.add(task); // повторное добавление

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void shouldRemoveTaskFromHistoryById() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1,"Task 1", "Description 1");
        Task task2 = new Task(2,"Task 2", "Description 2");

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void shouldKeepOrderOfViews() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1,"Task 1", "Description 1");
        Task task2 = new Task(2,"Task 2", "Description 2");
        Task task3 = new Task(3,"Task 3", "Description 3");

        historyManager.add(task1);

        historyManager.add(task2);

        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task1, task2, task3), history);
    }



}