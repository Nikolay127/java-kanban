package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.*;

import java.util.List;

public interface TaskManager {


    HistoryManager getInMemoryHistoryManager();

    Integer addTask(Task task);

    Integer addTask(int id, Task task);

    Integer addEpic(Task task);

    Integer addEpic(int id, Task task);

    Integer addSubtask(Task task);

    Integer addSubtask(int id, Task task);

    void updateTask(int id, Task task);

    void updateEpic(int id, Epic epic);

    void updateSubtask(int id, Subtask subtask);

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
