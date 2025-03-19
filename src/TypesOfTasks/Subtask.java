package TypesOfTasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String title, String description, int epicId) {
        super(id, title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
