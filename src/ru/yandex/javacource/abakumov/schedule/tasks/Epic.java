package ru.yandex.javacource.abakumov.schedule.tasks;

import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();


    public Epic(String name, String description) {
        super(name, description, Status.NEW);
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

    public void removeSubtasks(int id) {
        subtaskIds.remove(id);
    }


}
