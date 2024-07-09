package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.abakumov.schedule.tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager { //класс для хранения задач и операций над ними

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int generatorId = 0;
    private final HistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();


    //Проверяем пересечение задач по времени
    private boolean isOverlapping(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(task1.getDuration());
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(task2.getDuration());
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public int addTask(Task task) { //добавляем обычную задачу
        //Проверяем на пересечение по времени
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, task));
        if (isOverlapping) {
            throw new TaskValidationException("Задача пересекается с уже существующими");
        }
        int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
        return id;
    }

    @Override
    public int addEpic(Epic epic) { //добавляем эпик
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, epic));
        if (isOverlapping) {
            throw new TaskValidationException("Задача пересекается с уже существующими");
        }
        int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        prioritizedTasks.add(epic);
        return id;
    }

    @Override
    public Integer addSubtask(Subtask subtask) { //добавляем подзадачу
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, subtask));
        if (isOverlapping) {
            throw new TaskValidationException("Задача пересекается с уже существующими");
        }
        int epicId = subtask.getEpicID();
        Epic epic = epics.get(epicId);
        if (epic == null) {//проверяем, есть ли эпик, к которому мы хотим добавить нашу подзадачу
            throw new TaskValidationException("Epic c id = " + epicId + " не найден");
        }
        int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        updateEpicStatus(epicId);
        epic.addSubtaskId(id); //добавляем номер подзадачи в список подзадач у эпика
        updateEpicDuration(epic); //обновляем duration у соответствующего эпика
        prioritizedTasks.add(subtask);
        return id;
    }

    @Override
    public void updateTask(Task task) { //обновляем уже имеющуюся задачу путём замены
        final int id = task.getId();
        if (tasks.get(id) == null) {
            return;
        }
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, task));
        if (isOverlapping) {
            throw new TaskValidationException("Задача пересекается с уже существующими");
        }
        tasks.put(id, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return;
        }
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, epic));
        if (isOverlapping) {
            throw new TaskValidationException("Задача пересекается с уже существующими");
        }
        epic.setSubtaskIds(savedEpic.getSubtaskIds());
        epic.setStatus(savedEpic.getStatus());
        epic.setStartTime(savedEpic.getStartTime());
        epic.setEndTime(savedEpic.getEndTime());
        epic.setDuration(savedEpic.getDuration().toMinutes());
        epics.put(id, epic);
        prioritizedTasks.add(epic);
        /*
        "Во время обновления эпика, поля
        status и subtaskIds не должны обновляться, не забывай"

        Не очень понял это требование. Мы же создаём новый эпик и заменяем старый эпик на новый.
        Соответственно у нового эпика нет ни одной сабтаски. Точнее сабтаски есть,
        но в самом эпике про них нет информации. И получается, что они существуют в отрыве от какого-либо эпика.
        Что тогда делать с этими сабтасками старого эпика? Удалять?
         */
    }

    @Override
    public void updateSubtask(Subtask subtask) { //обновляем уже имеющуюся задачу путём замены
        final int id = subtask.getId();
        Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, subtask));
        if (isOverlapping) {
            throw new TaskValidationException("Задача пересекается с уже существующими");
        }
        int epicId = subtask.getEpicID();
        subtasks.put(id, subtask);
        prioritizedTasks.add(subtask);
        updateEpicStatus(epicId);
        updateEpicDuration(epics.get(epicId));
    }

    @Override
    public List<Task> getAllTasks() { //получаем список обычных задач
        for (Task task : tasks.values()) {
            inMemoryHistoryManager.add(task);
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() { //получаем список обычных задач
        for (Epic epic : epics.values()) {
            //добавляем в историю только эпики, без подзадач, т.к. их не смотрели
            inMemoryHistoryManager.add(epic);
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() { //получаем список обычных задач
        for (Subtask subtask : subtasks.values()) {
            inMemoryHistoryManager.add(subtask);
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) { //получаем обычную задачу
        final Task task = tasks.get(id);
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) { //получаем эпик
        final Epic epic = epics.get(id);
        inMemoryHistoryManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) { //получаем подзадачу
        final Subtask subtask = subtasks.get(id);
        inMemoryHistoryManager.add(subtask);
        return subtask;
    }

    @Override
    public void deleteTask(int id) { //удаляем конкретную простую задачу
        inMemoryHistoryManager.remove(id);
        tasks.remove(id);
    }

    @Override
    //вместе с эпиком удаляем все его подзадачи
    public void deleteEpic(int id) { //удаляем конкретный эпик
        final Epic epic = epics.remove(id);
        List<Task> historySubtasks = inMemoryHistoryManager.getHistory();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            //если подзадачу эпика просматривали, то её тоже удаляем из истории просмотра
            if (historySubtasks.contains(subtasks.get(subtaskId))) {
                inMemoryHistoryManager.remove(subtaskId);
            }
            subtasks.remove(subtaskId);
        }
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) { //удаляем конкретную подзадачу
        List<Task> historySubtasks = inMemoryHistoryManager.getHistory();
        if (historySubtasks.contains(subtasks.get(id))) {
            inMemoryHistoryManager.remove(id);
        }
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) { //если её и не было, то выходим из метода
            return;
        }
        Epic epic = epics.get(subtask.getEpicID());
        epic.removeSubtasks(id); //удаляем нужную подзадачу у релевантного эпика
        updateEpicStatus(epic.getId());
        updateEpicDuration(epic); //обновляем время и продолжительность эпика
    }

    @Override
    public void deleteAllTasks() { //удаляем все обычные задачи
        for (Task task : getAllTasks()) {
            inMemoryHistoryManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    //Вместе со всеми эпиками удаляем и все подзадачи
    public void deleteAllEpics() { //удаляем все эпики
        List<Task> historyEpics = inMemoryHistoryManager.getHistory();
        for (Epic epic : getAllEpics()) {
            if (historyEpics.contains(epic)) { //если эпик просматривали
                inMemoryHistoryManager.remove(epic.getId());
                for (Integer subtaskId : epic.getSubtaskIds()) {
                    //если подзадачу эпика просматривали, то её тоже удаляем из истории просмотра
                    if (historyEpics.contains(subtasks.get(subtaskId))) {
                        inMemoryHistoryManager.remove(subtaskId);
                    }
                }
            }
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() { //удаляем все подзадачи
        for (Epic epic : epics.values()) { //проходимся по соответствующим эпикам
            for (Integer subtaskId : epic.getSubtaskIds()) { //удаляем из истории нужные подзадачи
                inMemoryHistoryManager.remove(subtaskId);
            }
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    protected Map<Integer, Task> getTasksForFile() {
        return tasks;
    }

    protected Map<Integer, Epic> getEpicsForFile() {
        return epics;
    }

    protected Map<Integer, Subtask> getSubtasksForFile() {
        return subtasks;
    }

    protected int getIdsQuantity() {
        return generatorId;
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
            if (subtask.getStatus() != Status.DONE) {
                isEpicDone = false;
                break;
            }
        }
        boolean isEpicNew = true;
        for (Subtask subtask : relatedSubtasks) { //проверка на статус NEW(все подзадачи new)
            if (subtask.getStatus() != Status.NEW) {
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

    private void updateEpicDuration(Epic epic) {
        List<Integer> subs = epic.getSubtaskIds();
        if (subs.isEmpty()) {
            epic.setDuration(0L);
            return;
        }
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        long duration = 0L;
        for (int id : subs) {
            final Subtask subtask = subtasks.get(id);
            final LocalDateTime startTime = subtask.getStartTime();
            final LocalDateTime endTime = subtask.getEndTime();
            if (startTime.isBefore(start)) {
                start = startTime;
            }
            if (endTime.isAfter(end)) {
                end = endTime;
            }
            duration += subtask.getDuration().toMinutes();
        }
        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

}

