package test.java.typesoftasks.managers;

import typesoftasks.tasks.Epic;
import typesoftasks.managers.InMemoryTaskManager;
import typesoftasks.managers.NotFoundException;
import typesoftasks.tasks.Subtask;
import typesoftasks.tasks.Task;
import org.junit.jupiter.api.Test;
import typesoftasks.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void testCreateTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask("Task 1", "Description of Task 1");

        assertNotNull(task);
        assertEquals("Task 1", task.getTitle());
        assertEquals("Description of Task 1", task.getDescription());
    }

    @Test
    void testCreateEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");

        assertNotNull(epic);
        assertEquals("Epic 1", epic.getTitle());
        assertEquals("Description of Epic 1", epic.getDescription());
    }

    @Test
    void testAddSubtaskToEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description of Subtask 1", epic.getId());

        assertTrue(epic.getSubtasks().contains(subtask.getId()));
    }

    @Test
    void testGetTaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask("Task 1", "Description of Task 1");

        Task retrievedTask = taskManager.getTask(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
    }

    @Test
    void testGetEpicById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void testDeleteTaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask("Task 1", "Description of Task 1");
        taskManager.deleteTaskById(task.getId());

        assertThrows(NotFoundException.class, () -> taskManager.getTask(task.getId()));
    }

    @Test
    void testDeleteEpicById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");
        taskManager.deleteEpicById(epic.getId());

        assertThrows(NotFoundException.class, () -> taskManager.getEpic(epic.getId()));
    }

    @Test
    void testDeleteSubtaskById() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic 1", "Description of Epic 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description of Subtask 1", epic.getId());
        taskManager.deleteSubtaskById(subtask.getId());

        assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void testHistoryManager() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = taskManager.createTask("Task 1", "Description of Task 1");

        taskManager.getTask(task.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void epicStatusShouldBeNewIfAllSubtasksAreNew() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic", "desc");
        taskManager.createSubtask("Sub1", "desc", epic.getId());
        taskManager.createSubtask("Sub2", "desc", epic.getId());

        assertEquals(TaskStatus.NEW, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusShouldBeDoneIfAllSubtasksDone() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic", "desc");
        Subtask s1 = taskManager.createSubtask("Sub1", "desc", epic.getId());
        Subtask s2 = taskManager.createSubtask("Sub2", "desc", epic.getId());

        s1.setStatus(TaskStatus.DONE);
        s2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);

        assertEquals(TaskStatus.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressIfMixed() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic", "desc");
        Subtask s1 = taskManager.createSubtask("Sub1", "desc", epic.getId());
        Subtask s2 = taskManager.createSubtask("Sub2", "desc", epic.getId());

        s1.setStatus(TaskStatus.NEW);
        s2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicStatusShouldBeInProgressIfAllInProgress() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.createEpic("Epic", "desc");
        Subtask s1 = taskManager.createSubtask("Sub1", "desc", epic.getId());
        Subtask s2 = taskManager.createSubtask("Sub2", "desc", epic.getId());

        s1.setStatus(TaskStatus.IN_PROGRESS);
        s2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void shouldThrowExceptionIfTasksIntersect() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = taskManager.createTask("T1", "desc");
        task1.setStartTime(LocalDateTime.of(2025, 6, 10, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.updateTask(task1);

        Task task2 = taskManager.createTask("T2", "desc");
        task2.setStartTime(LocalDateTime.of(2025, 6, 10, 10, 30));
        task2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> taskManager.updateTask(task2));
    }
}
