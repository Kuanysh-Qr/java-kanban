package test;

import typesoftasks.managers.InMemoryTaskManager;
import typesoftasks.tasks.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testUniqueTaskId() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.createTask("Task 1", "Description of Task 1");
        Task task2 = taskManager.createTask("Task 2", "Description of Task 2");

        assertNotEquals(task1.getId(), task2.getId(), "ID задач должны быть уникальными.");
    }

}