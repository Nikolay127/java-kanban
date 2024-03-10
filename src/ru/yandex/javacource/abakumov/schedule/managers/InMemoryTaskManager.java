package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager{ //класс для хранения задач и операций над ними

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;
    private HistoryManager inMemoryHistoryManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.inMemoryHistoryManager = historyManager;
    }

    @Override
    public HistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
    }

    @Override
    public Integer addTask(Task task) { //добавляем обычную задачу
        int id = ++generatorId;
        while (true) { //проверяем, свободен ли такой id
            if (tasks.containsKey(id) && epics.containsKey(id) && subtasks.containsKey(id)) {
                id = ++ generatorId;
            } else {
                break;
            }
        }
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer addTask(int id, Task task) { //добавляем задачу с принудительным указанием id
        if (tasks.containsKey(id) && epics.containsKey(id) && subtasks.containsKey(id)) {
            System.out.println("Задача с таким id уже существует");
            return null;
        }
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public Integer addEpic(Task task) { //добавляем обычную задачу
        if (task instanceof Epic epic) {
            int id = ++generatorId;
            while (true) { //проверяем, свободен ли такой id
                if (tasks.containsKey(id) && epics.containsKey(id) && subtasks.containsKey(id)) {
                    id = ++ generatorId;
                } else {
                    break;
                }
            }
            epic.setId(id);
            epics.put(id, epic);
            return id;
        }
        return null;
    }

    @Override
    public Integer addEpic(int id, Task task) {
        if (tasks.containsKey(id) && epics.containsKey(id) && subtasks.containsKey(id)) {
            System.out.println("Задача с таким id уже существует");
            return null;
        }
        if (task instanceof Epic epic) {
            epic.setId(id);
            epics.put(id, epic);
            return id;
        }
        return null;
    }

    @Override
    public Integer addSubtask(Task task) { //добавляем обычную задачу
        if (task instanceof Subtask subtask) {
            int epicId = subtask.getEpicID();
            Epic epic = epics.get(epicId);
            if (epic == null) //проверяем, есть ли эпик, к которому мы хотим добавить нашу подзадачу
                return null;
            int id = ++generatorId;
            while (true) { //проверяем, свободен ли такой id
                if (tasks.containsKey(id) && epics.containsKey(id) && subtasks.containsKey(id)) {
                    id = ++ generatorId;
                } else {
                    break;
                }
            }
            subtask.setId(id);
            subtasks.put(id, subtask);
            updateEpicStatus(epicId);
            return id;
        }
        return null;
    }

    @Override
    public Integer addSubtask(int id, Task task) {
        if (task instanceof Subtask subtask) {
            if (tasks.containsKey(id) && epics.containsKey(id) && subtasks.containsKey(id)) {
                System.out.println("Задача с таким id уже существует");
                return null;
            }
            int epicId = subtask.getEpicID();
            Epic epic = epics.get(epicId);
            if (epic == null) //проверяем, есть ли эпик, к которому мы хотим добавить нашу подзадачу
                return null;
            subtask.setId(id);
            subtasks.put(id, subtask);
            updateEpicStatus(epicId);
            return id;
        }
        return null;
    }

    @Override
    public void updateTask(Task task) { //обновляем уже имеющуюся задачу путём замены
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }
    @Override
    public void updateEpic(Task task) { //обновляем уже имеющийся эпик. Обновляем только имя и описание
        if (task instanceof Epic epic) {
            int id = epic.getId();
            Epic savedEpic = epics.get(id);
            if (savedEpic == null) {
                return;
            }
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            epics.put(id, savedEpic);
        }
    }
    @Override
    public void updateSubtask(Task task) { //обновляем уже имеющуюся задачу путём замены
        if (task instanceof Subtask subtask) {
            int id = subtask.getId();
            Subtask savedSubtask = subtasks.get(id);
            if (savedSubtask == null) {
                return;
            }
            int epicId = subtask.getEpicID();
            subtasks.put(id, subtask);
            updateEpicStatus(epicId);
        }
    }

    private void updateEpicStatus(int epicID) { //устанавливаем новый статус эпика, исходя из статусов подзадач
        ArrayList<Subtask> relatedSubtasks = new ArrayList<>(); //список подзадач, подходящих под id нужного эпика
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicID() == epicID) {
                relatedSubtasks.add(subtask); //заполняем список подзадачами
            }
        }
        if (relatedSubtasks.isEmpty()) { //проверяем, есть ли хоть одна подзадача
            epics.get(epicID).setStatus(Status.NEW);
            return;
        }
        boolean isEpicDone = true;
        for (Subtask subtask : relatedSubtasks) { //проверка на статус DONE(все подзадачи done)
            if (subtask.getStatus() != Status.DONE){
                isEpicDone = false;
                break;
            }
        }
        boolean isEpicNew = true;
        for (Subtask subtask : relatedSubtasks) { //проверка на статус NEW(все подзадачи new)
            if (subtask.getStatus() != Status.NEW){
                isEpicNew = false;
                break;
            }
        }
        if (isEpicDone) {
            epics.get(epicID).setStatus(Status.DONE);
        } else if (isEpicNew) {
            epics.get(epicID).setStatus(Status.NEW);
        } else { //если не все подзадачи NEW и не все подзадачи DONE, то эпик в статусе IN_PROGRESS
            epics.get(epicID).setStatus(Status.IN_PROGRESS);
        }
    }
    @Override
    public List<Task> getAllTasks() { //получаем список обычных задач
        return new ArrayList<>(tasks.values());
    }
    @Override
    public List<Epic> getAllEpics() { //получаем список обычных задач
        return new ArrayList<>(epics.values());
    }
    @Override
    public List<Subtask> getAllSubtasks() { //получаем список обычных задач
        return new ArrayList<>(subtasks.values());
    }
    @Override
    public Task getTask(int id) { //получаем обычную задачу
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }
    @Override
    public Epic getEpic(int id) { //получаем эпик
        if (epics.containsKey(id)) {
            inMemoryHistoryManager.add(epics.get(id));
        }
        return epics.get(id);
    }
    @Override
    public Subtask getSubtask(int id) { //получаем подзадачу
        if (subtasks.containsKey(id)) {
            inMemoryHistoryManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }
    @Override
    public void deleteTask(int id) { //удаляем конкретную простую задачу
        tasks.remove(id);
    }
    @Override
    //вместе с эпиком удаляем все его подзадачи
    public void deleteEpic(int id) { //удаляем конкретный эпик
        final Epic epic = epics.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
    }
    @Override
    public void deleteSubtask(int id) { //удаляем конкретную подзадачу
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) { //если её и не было, то выходим из метода
            return;
        }
        Epic epic = epics.get(subtask.getEpicID());
        epic.removeSubtasks(id); //удаляем нужную подзадачу у релевантного эпика
        updateEpicStatus(epic.getId());
    }
    @Override
    public void deleteAllTasks() { //удаляем все обычные задачи
        tasks.clear();
    }
    @Override
    //Вместе со всеми эпиками удаляем и все подзадачи
    public void deleteAllEpics() { //удаляем все эпики
        subtasks.clear();
        epics.clear();
    }
    @Override
    public void deleteAllSubtasks() { //удаляем все подзадачи
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }



}

