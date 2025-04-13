package TypesTest;

import TypesOfTasks.Epic;
import TypesOfTasks.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

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

}