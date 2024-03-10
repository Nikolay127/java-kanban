package ru.yandex.javacource.abakumov.schedule.managers;

import ru.yandex.javacource.abakumov.schedule.tasks.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(1, testTaskManager.getInMemoryHistoryManager().getHistory().size()); //проверяем вызов метода getTask

        int idEpic = testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = testTaskManager.getEpic(idEpic);
        assertEquals(2, testTaskManager.getInMemoryHistoryManager().getHistory().size());
    }

    @Test //проверяем, что нет конфликта между вручную назначенными id и сгенерированными id
    public void tasksWithAGivenIdAndAGeneratedIdDontConflictWithinTheManager() {
        TaskManager testTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        testTaskManager.addTask(2, new Task("Задача 2", "Описание задачи 2", Status.NEW));
        assertNotNull(testTaskManager.getAllTasks(), "Задачи не были добавлены");
    }

    @Test //проверяем, что объект Epic нельзя добавить в самого себя в виде подзадачи
    public void epicObjectCantBeAddedToItselfAsSubtask() {
        TaskManager testTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        assertNull(testTaskManager.addSubtask(new Epic("Тестовый эпик", "Описание эпика")));
    }

    @Test //проверяем, что объект Subtask нельзя сделать эпиком
    public void subtaskObjectCantBeAddedToItselfAsEpic() {
        TaskManager testTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        assertNull(testTaskManager.addEpic(new Subtask(1, "Подзадача эпика 1", "Описание эпика", Status.NEW)));
    }


}