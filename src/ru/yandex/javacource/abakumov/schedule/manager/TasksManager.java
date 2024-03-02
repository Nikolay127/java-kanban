package ru.yandex.javacource.abakumov.schedule.manager;
import ru.yandex.javacource.abakumov.schedule.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TasksManager { //класс для хранения задач и операций над ними

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int generatorId = 0;


    public int addTask(Task task) { //добавляем обычную задачу
        int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addEpic(Epic epic) { //добавляем обычную задачу
        int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public Integer addSubtask(Subtask subtask) { //добавляем обычную задачу
        int epicId = subtask.getEpicID();
        Epic epic = epics.get(epicId);
        if (epic == null)
            return null;
        int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        updateEpicStatus(epicId);
        return id;
    }

    public void updateTask(Task task) { //обновляем уже имеющуюся задачу
        /*
        Не очень понял, почему мы где-то ставим final в переменных метода, а где-то(например в методе выше - нет).
        И здесь, и там мы получаем идентификатор нужного объекта. Но здесь есть модификатор final, а там - нет.
        Буду благодарен, если дадите краткое объяснение.
        P.S. И вообще хотел выразить Вам благодарность за подробные и максимально понятные ревью, помогающие
        сильнее разобраться в пройденной теме. Большое спасибо! Приятно иметь такого ревьюера как Вы!
         */
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null)
            return;
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic) { //обновляем уже имеющийся эпик
        int id = epic.getId();
        Epic savedEpic = epics.get(id);
        /*
        Так же вот здесь вы в ревью передали в метод get() сразу epic.getId(), без предварительно сохранения id.
        А в большинстве остальных мест - сохраняли.
        Насколько я понимаю, передача id без сохранения в переменную - более удачное решение с точки зрения производительности.
        Но где-то мы используем это значение по нескольку раз, потому правильнее сохранить значение и брать его уже
        из переменной, а не вызывать несколько раз нужный метод. Прав ли я или здесь какая-то другая логика?
        Так же буду очень благодарен за небольшой комментарий по этому вопросу.
         */
        if (savedEpic == null)
            return;
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        epics.put(id, savedEpic);
    }

    public void updateSubtask(Subtask subtask) { //обновляем уже имеющуюся задачу путём замены
        int id = subtask.getId();
        int epicId = subtask.getEpicID();
        Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null)
            return;
        subtasks.put(id, subtask);
        updateEpicStatus(epicId);
    }

    private void updateEpicStatus(int epicID) { //устанавливаем новый статус эпика по его идентификатору
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

    public ArrayList<Task> getAllTasks() { //получаем список обычных задач
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() { //получаем список обычных задач
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() { //получаем список обычных задач
        return new ArrayList<>(subtasks.values());
    }

    public Task getTask(int id) { //получаем конкретную обычную задачу
        return tasks.get(id);
    }

    public Epic getEpic(int id) { //получаем конкретную обычную задачу
        return epics.get(id);
    }

    public Subtask getSubtask(int id) { //получаем конкретную обычную задачу
        return subtasks.get(id);
    }

    public void deleteTask(int id) { //удаляем конкретную простую задачу
        tasks.remove(id);
    }

    //вместе с эпиком удаляем все его подзадачи
    public void deleteEpic(int id) { //удаляем конкретный эпик
        final Epic epic = epics.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
    }

    public void deleteSubtask(int id) { //удаляем конкретную подзадачу
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) { //если её и не было, то выходим из метода
            return;
        }
        Epic epic = epics.get(subtask.getEpicID());
        epic.removeSubtasks(id); //удаляем нужную подзадачу у релевантного эпика
        updateEpicStatus(epic.getId());
    }

    public void deleteAllTasks() { //удаляем все обычные задачи
        tasks.clear();
        System.out.println("Все обычные задачи удалены\n");
    }

    //Вместе со всеми эпиками удаляем и все подзадачи
    public void deleteAllEpics() { //удаляем все эпики
        subtasks.clear();
        epics.clear();
        System.out.println("Все эпики удалены\n");
    }

    public void deleteAllSubtasks() { //удаляем все подзадачи
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

}

