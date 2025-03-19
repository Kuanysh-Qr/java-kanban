package TypesOfTasks;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = manager.createTask("купить книгу", "физика, химия, биология");
        Task task2 = manager.createTask("прочитать книгу", "страница 1-3");

        Epic epic1 = manager.createEpic("ремонт", "Кухня, ванная");
        Subtask sub1 = manager.createSubtask("покрасить стены", "выбрать цвет", epic1.getId());
        Subtask sub2 = manager.createSubtask("заменить плитку", "купить плитку", epic1.getId());

        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());

        System.out.println("Все эпики:");
        System.out.println(manager.getAllEpics());

        System.out.println("Все подзадачи:");
        System.out.println(manager.getAllSubtasks());

        sub1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub1);
        System.out.println("Статус эпика после обновления одной подзадачи: " + epic1.getStatus());

        sub2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(sub2);
        System.out.println("Статус эпика после завершения всех подзадач: " + epic1.getStatus());

        manager.deleteTaskById(task1.getId());
        System.out.println("Все задачи после удаления одной:");
        System.out.println(manager.getAllTasks());

        manager.deleteEpicById(epic1.getId());
        System.out.println("Все эпики после удаления одного:");
        System.out.println(manager.getAllEpics());
    }
}
