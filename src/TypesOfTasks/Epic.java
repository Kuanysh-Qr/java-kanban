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
        subtaskIds.add(subtaskId);
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void updateStatus(TaskManager manager) {
        if (subtaskIds.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean hasInProgress = false;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = manager.getSubtaskById(subtaskId);
            if (subtask == null) continue;

            if (subtask.getStatus() == TaskStatus.NEW) {
                allDone = false;
            } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                allDone = false;
                hasInProgress = true;
            }
        }

        if (allDone) {
            setStatus(TaskStatus.DONE);
        } else if (hasInProgress) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else {
            setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
