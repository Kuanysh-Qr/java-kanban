package typesoftasks.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String title, String description) {
        super(id, title, description, TaskType.EPIC);
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

    public void recalculateTimeFields(List<Subtask> allSubtasks) {
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Integer subId : subtaskIds) {
            for (Subtask subtask : allSubtasks) {
                if (subtask.getId() == subId) {
                    Duration dur = subtask.getDuration();
                    LocalDateTime start = subtask.getStartTime();
                    LocalDateTime end = subtask.getEndTime();

                    if (dur != null) {
                        totalDuration = totalDuration.plus(dur);
                    }

                    if (start != null && (earliestStart == null || start.isBefore(earliestStart))) {
                        earliestStart = start;
                    }

                    if (end != null && (latestEnd == null || end.isAfter(latestEnd))) {
                        latestEnd = end;
                    }
                }
            }
        }

        setDuration(totalDuration);
        setStartTime(earliestStart);
        this.endTime = latestEnd;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                "} " + super.toString();
    }
}
