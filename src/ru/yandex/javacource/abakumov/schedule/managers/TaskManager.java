package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.*;

import java.util.List;

public interface TaskManager {


    List<Task> getPrioritizedTasks();

    List<Task> getHistory();

    int addTask(Task task);

    int addEpic(Epic epic);

    Integer addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void deleteTask(int id);

    //вместе с эпиком удаляем все его подзадачи
    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteAllTasks();

    //Вместе со всеми эпиками удаляем и все подзадачи
    public void deleteAllEpics();

    void deleteAllSubtasks();

}
