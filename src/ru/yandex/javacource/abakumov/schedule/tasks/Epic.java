package ru.yandex.javacource.abakumov.schedule.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    protected LocalDateTime endTime = LocalDateTime.MAX;

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

    public void setDuration(Long duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
