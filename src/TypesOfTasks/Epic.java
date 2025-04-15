package TypesOfTasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds;

    public Epic(int id, String title, String description) {
        super(id, title, description);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtasks() {
        return Collections.unmodifiableList(subtaskIds);
    }

    public void addSubtask(int subtaskId) {
        if (subtaskId == getId()) {
            throw new IllegalArgumentException("Epic cannot be its own subtask");
        }
        subtaskIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                "} " + super.toString();
    }
}
