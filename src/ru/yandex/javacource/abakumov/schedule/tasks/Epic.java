package ru.yandex.javacource.abakumov.schedule.tasks;

import java.time.Duration;
import java.util.ArrayList;
import java.util.TreeSet;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    //обновляем время начала и окончания, а также заново суммируем продолжительность
    public void updateEpicTimeAndDuration(TreeSet<Subtask> subtasks) {
        if (subtaskIds.isEmpty()) { //если обновление происходит после удаления всех подзадач
            startTime = null;
            endTime = null;
            duration = Duration.ZERO;
            return;
        }
        startTime = subtasks.first().startTime;
        endTime = subtasks.last().endTime;
        duration = subtasks.stream()
                .filter(subtask -> subtaskIds.contains(subtask.getId()))
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
        startTime = null;
        duration = Duration.ZERO;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtasks(int id) {
        subtaskIds.remove((Integer) id); //удаляем именно нужный элемент по значению, а не по индексу
    }


}
