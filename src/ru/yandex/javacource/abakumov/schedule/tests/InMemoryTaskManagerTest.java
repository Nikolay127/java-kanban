package ru.yandex.javacource.abakumov.schedule.tests;
/*
Не смог создать папку test на одном уровне с src.
Если создаю там директорию test, то не могу туда перенести нужный класс
 */

import ru.yandex.javacource.abakumov.schedule.managers.*;
import ru.yandex.javacource.abakumov.schedule.tasks.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InMemoryTaskManagerTest {


    @Test //проверяем, что задачи равны, если равны их id
    public void twoInstancesOfDifferentTaskEqualIfTheIdsEqual(){
        TaskManager firstTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        TaskManager secondTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

        firstTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        secondTaskManager.addTask(new Task("Задача 2", "Описание задачи 2", Status.NEW));
        firstTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        secondTaskManager.addEpic(new Epic("Эпик 2", "Описание эпика 2"));
        firstTaskManager.addSubtask(new Subtask(2,"Подзадача эпика 1", "Описание подзадачи эпика 1", Status.NEW));
        secondTaskManager.addSubtask(new Subtask(2,"Подзадача эпика 2", "Описание подзадачи эпика 2", Status.NEW));

        assertEquals(firstTaskManager.getTask(1), secondTaskManager.getTask(1), "Объекты задач не равны");
        assertEquals(firstTaskManager.getEpic(2), secondTaskManager.getEpic(2), "Объекты эпика не равны");
        assertEquals(firstTaskManager.getSubtask(3), secondTaskManager.getSubtask(3), "Объекты подзадач не равны");
    }

    @Test //проверяем, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    public void theUtilityClassAlwaysReturnsInitializedAndReadyToUseInstancesOfManagers() {
        assertNotNull(Managers.getDefault(), "Объект не проинициализирован");
        assertNotNull(Managers.getDefaultHistory(), "Объект не проинициализирован");
    }

    @Test //проверяем, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    public void inMemoryTaskManagerReallyAddsDifferentKindsOfTasksAndFindThemByIds() {
        TaskManager testTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        assertNotNull(testTaskManager.getAllTasks(), "Задача не добавлена"); //проверяем, что список задач не пустой

        testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        assertNotNull(testTaskManager.getAllEpics(), "Эпик не добавлен"); //проверяем, что список эпиков не пустой

        testTaskManager.addSubtask(new Subtask(2,"Подзадача эпика 1", "Описание подзадачи эпика 1", Status.NEW));
        assertNotNull(testTaskManager.getAllSubtasks(), "Подзадача не добавлена"); //проверяем, что список подзадач не пустой

        assertNotNull(testTaskManager.getTask(1), "Задача не была получена");
        assertNotNull(testTaskManager.getEpic(2), "Эпик не был получен");
        assertNotNull(testTaskManager.getSubtask(3), "Подзадача не была получена");
    }

    @Test //проверяем неизменность задачи (по всем полям) при добавлении задачи в менеджер
    public void theImmutabilityOfTheTaskInAllFieldsWhenAddingTheTaskToTheManager(){
        TaskManager testTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        testTaskManager.addSubtask(new Subtask(2,"Подзадача эпика 1", "Описание подзадачи эпика 1", Status.NEW));

        //Проверяем обычную задачу
        assertEquals(testTaskManager.getTask(1).getName(), "Задача 1");
        assertEquals(testTaskManager.getTask(1).getDescription(), "Описание задачи 1");
        assertEquals(testTaskManager.getTask(1).getStatus(), Status.NEW);

        //Проверяем эпик
        assertEquals(testTaskManager.getEpic(2).getName(), "Эпик 1");
        assertEquals(testTaskManager.getEpic(2).getDescription(), "Описание эпика 1");

        //Проверяем подзадачу
        assertEquals(testTaskManager.getSubtask(3).getEpicID(), 2);
        assertEquals(testTaskManager.getSubtask(3).getName(), "Подзадача эпика 1");
        assertEquals(testTaskManager.getSubtask(3).getDescription(), "Описание подзадачи эпика 1");
        assertEquals(testTaskManager.getSubtask(3).getStatus(), Status.NEW);
    }

    @Test //проверяем, что задачи добавляются в истории "просмотра"
    public void tasksAddedToTheHistoryManager() {
        TaskManager testTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

        int idTask = testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        Task task = testTaskManager.getTask(idTask);
        assertEquals(1, testTaskManager.getHistory().size()); //проверяем вызов метода getTask

        int idEpic = testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = testTaskManager.getEpic(idEpic);
        assertEquals(2, testTaskManager.getHistory().size());
    }

    @Test //проверяем, что нет конфликта между вручную назначенными id и сгенерированными id
    public void tasksWithAGivenIdAndAGeneratedIdDontConflictWithinTheManager() {
        TaskManager testTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", Status.NEW);
        task.setId(10);
        testTaskManager.addTask(task);
        //проверяем, что у задачи будет тот id, который генерируем сами, а не тот, который был у задачи изначально
        assertEquals(1, testTaskManager.getTask(1).getId());
    }


    /*
    ---проверяем, что объект Epic нельзя добавить в самого себя в виде подзадачи---
    ---проверяем, что объект Subtask нельзя сделать эпиком---
    Учитывая последнее ревью, где я вернул в методы по добавлению эпиков и сабтасков изначальные принимаемые параметры,
    произойдёт ошибка компиляции, если я буду пробовать передать эпик туда, где принимается подзадача и наоборот.
    Поэтому в задании этого спринта я и изменял входной параметр, чтобы у меня была физическая возможность передать
    эпик в задачу по добавлению(обновлению) эпика.
    Возможно я неправильно понимаю требования к этим двум тестам.
     */


    @Test //проверяем, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    public void tasksAddedToHistoryManagerKeepThePreviousVersionOfTheTaskAndItsData() {
        TaskManager testTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        //добавляем задачу и запоминаем её id
        int id = testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        //добавляем в историю просмотров путём получения задачи
        testTaskManager.getTask(id);
        //создаём новую задачу, которую позже передадим в качестве обновлённой
        Task updatedTask = new Task("Задача 1", "Новое описание задачи 1", Status.IN_PROGRESS);
        //присваиваем обновлённой задаче id старой
        updatedTask.setId(id);
        testTaskManager.updateTask(updatedTask);
        //получаем старое описание задачи, которое хранится в истории
        String oldDescription = testTaskManager.getHistory().get(0).getDescription();
        //получаем новое описание задачи из списка задач менеджера
        String newDescription = testTaskManager.getAllTasks().get(0).getDescription();
        assertNotEquals(oldDescription, newDescription, "Предыдущее описание задачи не сохранилась");
    }

}