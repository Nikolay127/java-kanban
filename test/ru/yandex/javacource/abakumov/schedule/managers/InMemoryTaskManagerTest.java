package ru.yandex.javacource.abakumov.schedule.managers;

import static org.junit.jupiter.api.Assertions.*;
import ru.yandex.javacource.abakumov.schedule.tasks.*;
import org.junit.jupiter.api.Test;
import java.util.List;

class InMemoryTaskManagerTest {

    @Test //проверяем, что задачи равны, если равны их id
    public void twoInstancesOfDifferentTaskEqualIfTheIdsEqual(){
        TaskManager firstTaskManager = new InMemoryTaskManager();
        TaskManager secondTaskManager = new InMemoryTaskManager();

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
        TaskManager testTaskManager = new InMemoryTaskManager();

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
        TaskManager testTaskManager = new InMemoryTaskManager();
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
        TaskManager testTaskManager = new InMemoryTaskManager();

        int idTask = testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        Task task = testTaskManager.getTask(idTask);
        assertEquals(1, testTaskManager.getHistory().size()); //проверяем вызов метода getTask

        int idEpic = testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = testTaskManager.getEpic(idEpic);
        assertEquals(2, testTaskManager.getHistory().size());
    }

    @Test //проверяем, что нет конфликта между вручную назначенными id и сгенерированными id
    public void tasksWithAGivenIdAndAGeneratedIdDontConflictWithinTheManager() {
        TaskManager testTaskManager = new InMemoryTaskManager();
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", Status.NEW);
        task.setId(10);
        testTaskManager.addTask(task);
        //проверяем, что у задачи будет тот id, который генерируем сами, а не тот, который был у задачи изначально
        assertEquals(1, testTaskManager.getTask(1).getId());
    }

    @Test //проверяем, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    public void tasksAddedToHistoryManagerKeepThePreviousVersionOfTheTaskAndItsData() {
        TaskManager testTaskManager = new InMemoryTaskManager();
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

    @Test //проверяем, что история хранит ровно одну запись при двойном запрашивании задачи
    public void historyStoresExactlyOneRecordWhenATaskWasRequestedTwice() {
        TaskManager testTaskManager = new InMemoryTaskManager();
        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        Task requestedTask = testTaskManager.getTask(1);
        requestedTask = testTaskManager.getTask(1);
        assertEquals(testTaskManager.getHistory().size(), 1);
    }

    @Test //проверяем, что история хранит обновленную инфу при двойном запрашивании
    public void historyStoresUpdatedInfoWhenATaskWasRequestedAfterChanging() {
        TaskManager testTaskManager = new InMemoryTaskManager();
        //добавляем задачу и запоминаем её id
        int id = testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        //добавляем в историю просмотров путём получения задачи
        testTaskManager.getTask(id);
        //создаём новую задачу, которую позже передадим в качестве обновлённой
        Task updatedTask = new Task("Задача 1", "Новое описание задачи 1", Status.IN_PROGRESS);
        //присваиваем обновлённой задаче id старой
        updatedTask.setId(id);
        testTaskManager.updateTask(updatedTask);
        testTaskManager.getTask(id);
        assertEquals(testTaskManager.getHistory().get(0).getDescription(), "Новое описание задачи 1");
        assertEquals(testTaskManager.getHistory().get(0).getStatus(), Status.IN_PROGRESS);
    }

    @Test
    //Проверяем, что история корректно переписывается при новом запрашивании задачи, если в истории несколько задач
    //Прохождение этого теста также говорит о том, что метод по удалению ноды работает корректно
    public void historyRewritesTailWhenTheTaskRequestedTwice() {
        TaskManager testTaskManager = new InMemoryTaskManager();
        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        testTaskManager.addTask(new Task("Задача 2", "Описание задачи 2", Status.NEW));
        Task requestedTask1 = testTaskManager.getTask(1);
        Task requestedTask2 = testTaskManager.getTask(2);
        requestedTask1 = testTaskManager.getTask(1);
        assertEquals(testTaskManager.getHistory().get(0), requestedTask2);
        assertEquals(testTaskManager.getHistory().get(testTaskManager.getHistory().size()-1), requestedTask1);
    }

    @Test //Внутри эпиков не должно оставаться неактуальных id подзадач
    public void thereShouldBeNoIrrelevantSubtaskIDsLeftInsideTheEpics() {
        TaskManager testTaskManager = new InMemoryTaskManager();
        testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        testTaskManager.addSubtask(new Subtask(1,"Подзадача эпика 1", "Описание подзадачи эпика 1", Status.NEW));
        testTaskManager.deleteSubtask(2);
        assertEquals(testTaskManager.getEpic(1).getSubtaskIds().size(), 0);
    }

    @Test //При удалении эпика из истории удаляются и его подзадачи
    public void whenEpicIsDeletedFromHistoryItsSubtasksAreAlsoDeleted() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1,"Закончить 4й спринт", "Сдать финальное задание 6го спринта", Status.NEW));
        taskManager.getEpic(1);
        taskManager.getSubtask(2);
        assertEquals(taskManager.getHistory().size(), 2);
        taskManager.deleteEpic(1);
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test //при удалении всех обычных задач, они корректно удаляются из истории
    public void whenTasksAreDeletedHistoryIsAlsoClearedCorrectly() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask(new Task("Трекер задач", "Написать программу трекер-задач для четвертого спринта", Status.IN_PROGRESS)); //id-1
        taskManager.addTask(new Task("Купить продукты", "1.Молоко, 2.Хлеб, 3. Печенье", Status.NEW)); //id-2
        taskManager.getTask(1);
        taskManager.getTask(2);
        assertEquals(taskManager.getHistory().size(), 2);
        taskManager.deleteAllTasks();
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test //при удалении всех подзадач, они корректно удаляются из истории
    public void whenSubtasksAreDeletedHistoryIsAlsoClearedCorrectly() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1, "Закончить 4й спринт", "Сдать финальное задание 4го спринта", Status.NEW));
        taskManager.addSubtask(new Subtask(1, "Закончить 5й спринт", "Сделать финальное задание 5го спринта", Status.NEW));
        taskManager.getSubtask(2);
        taskManager.getSubtask(3);
        assertEquals(taskManager.getHistory().size(), 2);
        taskManager.deleteAllSubtasks();
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test //при удалении всех эпиков, они и их подзадачи корректно удаляются из истории
    public void whenAllEpicsAreDeletedTheyAndTheirSubtasksAreCorrectlyDeletedFromHistory() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1, "Закончить 4й спринт", "Сдать финальное задание 4го спринта", Status.NEW));
        taskManager.addSubtask(new Subtask(1, "Закончить 5й спринт", "Сделать финальное задание 5го спринта", Status.NEW));
        taskManager.getEpic(1);
        taskManager.getSubtask(2);
        taskManager.getSubtask(3);
        int size = taskManager.getHistory().size();
        List<Task> checking = taskManager.getHistory();
        assertEquals(taskManager.getHistory().size(), 3);
        taskManager.deleteAllEpics();
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test //корректно работает метод получения всех обычных задаач
    public void getAllTasksMethodWorksCorrectly() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask(new Task("Трекер задач", "Написать программу трекер-задач для четвертого спринта", Status.IN_PROGRESS));
        taskManager.addTask(new Task("Купить продукты", "1.Молоко, 2.Хлеб, 3. Печенье", Status.NEW));
        assertEquals(taskManager.getHistory().size(), 0);
        taskManager.getAllTasks();
        assertEquals(taskManager.getHistory().size(), 2);
    }

    @Test //корректно работает метод получения всех эпиков
    public void getAllEpicsMethodWorksCorrectly() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addEpic(new Epic("Устроится на работу java-программистов", "Освоить Java и Spring"));
        assertEquals(taskManager.getHistory().size(), 0);
        taskManager.getAllEpics();
        assertEquals(taskManager.getHistory().size(), 2);
    }


    @Test //корректно работает метод получения всех подзадач
    public void getAllSubtasksMethodWorksCorrectly() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1,"Закончить 4й спринт", "Сдать финальное задание 4го спринта", Status.NEW));
        taskManager.addSubtask(new Subtask(1, "Закончить 5й спринт", "Сделать финальное задание 5го спринта", Status.NEW));
        taskManager.addSubtask(new Subtask(1, "Закончить 6й спринт", "Сделать финальное задание 6го спринта", Status.NEW));
        assertEquals(taskManager.getHistory().size(), 0);
        taskManager.getAllSubtasks();
        assertEquals(taskManager.getHistory().size(), 3);
    }

    /*
    "Удаляемые подзадачи не должны хранить внутри себя старые id".

    Абсолютно не понял данного требования. Внутри позадач лежат только айдишники эпиков.
    При удалении позадачи, удаляется только её собственное айди у эпика. Корректность такого удаления проверена в тесте выше.
     */


}