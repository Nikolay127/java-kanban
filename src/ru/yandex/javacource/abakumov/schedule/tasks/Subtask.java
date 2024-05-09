package ru.yandex.javacource.abakumov.schedule.tasks;


public class Subtask extends Task {

    private int epicID; //принадлежность к эпику

    public Subtask(int epicID, String name, String description, Status status) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public Subtask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
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
