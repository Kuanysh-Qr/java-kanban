package TypesOfTasks;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task1 = manager.createTask("купить книгу", "физика, химия, биология");
        Task task2 = manager.createTask("прочитать книгу", "страница 1-3");

        Epic epic1 = manager.createEpic("ремонт", "Кухня, ванная");
        Subtask sub1 = manager.createSubtask("покрасить стены", "выбрать цвет", epic1.getId());
        Subtask sub2 = manager.createSubtask("заменить плитку", "купить плитку", epic1.getId());

        printTasks(manager.getAllTasks());
        System.out.println("История просмотров:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void printTasks(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
