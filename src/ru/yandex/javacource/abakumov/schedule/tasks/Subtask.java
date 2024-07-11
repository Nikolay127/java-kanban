package ru.yandex.javacource.abakumov.schedule.tasks;


import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicID; //принадлежность к эпику

    public Subtask(int epicID, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(int id, String name, String description, Status status, int epicID, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicID = epicID;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "ru.yandex.javacource.abakumov.schedule.tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + getId() +
                ", epicId=" + getEpicID() +
                ", status=" + status +
                '}';
    }
}
