package ru.yandex.javacource.abakumov.schedule.tasks;

import java.util.ArrayList;

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

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
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
