package typestest;

import typesoftasks.tasks.Epic;
import typesoftasks.managers.InMemoryTaskManager;
import typesoftasks.tasks.Subtask;
import typesoftasks.tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void testCreateTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask("Task 1", "Description of Task 1");

        assertNotNull(task, "Задача не должна быть null");
        assertEquals("Task 1", task.getTitle(), "Заголовок задачи не совпадает.");
        assertEquals("Description of Task 1", task.getDescription(), "Описание задачи не совпадает.");
    }

    @Test
    void testCreateEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");

        assertNotNull(epic, "Эпик не должен быть null");
        assertEquals("Epic 1", epic.getTitle(), "Заголовок эпика не совпадает.");
        assertEquals("Description of Epic 1", epic.getDescription(), "Описание эпика не совпадает.");
    }

    @Test
    void testAddSubtaskToEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description of Subtask 1", epic.getId());

        assertTrue(epic.getSubtasks().contains(subtask.getId()), "Подзадача должна быть добавлена к эпик.");
    }

    @Test
    void testGetTaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask("Task 1", "Description of Task 1");

        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask, "Задача должна быть найдена.");
        assertEquals(task, retrievedTask, "Задачи не совпадают.");
    }

    @Test
    void testGetEpicById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic, "Эпик должен быть найден.");
        assertEquals(epic, retrievedEpic, "Эпики не совпадают.");
    }

    @Test
    void testDeleteTaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask("Task 1", "Description of Task 1");
        taskManager.deleteTaskById(task.getId());

        Task retrievedTask = taskManager.getTask(task.getId());
        assertNull(retrievedTask, "Задача должна быть удалена.");
    }

    @Test
    void testDeleteEpicById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");
        taskManager.deleteEpicById(epic.getId());

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertNull(retrievedEpic, "Эпик должен быть удален.");
    }

    @Test
    void testDeleteSubtaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description of Subtask 1", epic.getId());
        taskManager.deleteSubtaskById(subtask.getId());

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNull(retrievedSubtask, "Подзадача должна быть удалена.");
    }

    @Test
    void testHistoryManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask("Task 1", "Description of Task 1");

        taskManager.getTask(task.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу.");
        assertEquals(task, history.get(0), "Задача в истории не совпадает.");
    }

}