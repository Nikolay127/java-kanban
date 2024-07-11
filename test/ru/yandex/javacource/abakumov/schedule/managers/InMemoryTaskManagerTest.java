package ru.yandex.javacource.abakumov.schedule.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.javacource.abakumov.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.abakumov.schedule.tasks.*;
import org.junit.jupiter.api.Test;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class InMemoryTaskManagerTest {

    @Test //проверяем, что задачи равны, если равны их id
    public void twoInstancesOfDifferentTaskEqualIfTheIdsEqual(){
        TaskManager firstTaskManager = new InMemoryTaskManager();
        TaskManager secondTaskManager = new InMemoryTaskManager();

        firstTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        secondTaskManager.addTask(new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2020, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        firstTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        secondTaskManager.addEpic(new Epic("Эпик 2", "Описание эпика 2"));
        firstTaskManager.addSubtask(new Subtask(2,"Подзадача эпика 1", "Описание подзадачи эпика 1",
                Status.NEW, LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        secondTaskManager.addSubtask(new Subtask(2,"Подзадача эпика 2", "Описание подзадачи эпика 2",
                Status.NEW, LocalDateTime.of(2022, 7, 2, 10, 0),
                Duration.ofMinutes(30)));

        assertEquals("Объекты задач не равны", firstTaskManager.getTask(1), secondTaskManager.getTask(1));
        assertEquals("Объекты эпика не равны", firstTaskManager.getEpic(2), secondTaskManager.getEpic(2));
        assertEquals("Объекты подзадач не равны", firstTaskManager.getSubtask(3), secondTaskManager.getSubtask(3));
    }

    @Test //проверяем, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    public void theUtilityClassAlwaysReturnsInitializedAndReadyToUseInstancesOfManagers() {
        assertNotNull("Объект не проинициализирован", Managers.getDefault());
        assertNotNull("Объект не проинициализирован", Managers.getDefaultHistory());
    }

    @Test //проверяем, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    public void inMemoryTaskManagerReallyAddsDifferentKindsOfTasksAndFindThemByIds() {
        TaskManager testTaskManager = new InMemoryTaskManager();

        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        assertNotNull("Задача не добавлена", testTaskManager.getAllTasks()); //проверяем, что список задач не пустой

        testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        assertNotNull("Эпик не добавлен", testTaskManager.getAllEpics()); //проверяем, что список эпиков не пустой

        testTaskManager.addSubtask(new Subtask(2,"Подзадача эпика 1", "Описание подзадачи эпика 1", Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        assertNotNull("Подзадача не добавлена", testTaskManager.getAllSubtasks()); //проверяем, что список подзадач не пустой

        assertNotNull("Задача не была получена", testTaskManager.getTask(1));
        assertNotNull("Эпик не был получен", testTaskManager.getEpic(2));
        assertNotNull("Подзадача не была получена", testTaskManager.getSubtask(3));
    }

    @Test //проверяем неизменность задачи (по всем полям) при добавлении задачи в менеджер
    public void theImmutabilityOfTheTaskInAllFieldsWhenAddingTheTaskToTheManager(){
        TaskManager testTaskManager = new InMemoryTaskManager();
        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        testTaskManager.addSubtask(new Subtask(2,"Подзадача эпика 1", "Описание подзадачи эпика 1", Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));

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

        int idTask = testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        Task task = testTaskManager.getTask(idTask);
        assertEquals(1, testTaskManager.getHistory().size()); //проверяем вызов метода getTask

        int idEpic = testTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = testTaskManager.getEpic(idEpic);
        assertEquals(2, testTaskManager.getHistory().size());
    }

    @Test //проверяем, что нет конфликта между вручную назначенными id и сгенерированными id
    public void tasksWithAGivenIdAndAGeneratedIdDontConflictWithinTheManager() {
        TaskManager testTaskManager = new InMemoryTaskManager();
        Task task = new Task("Тестовая задача", "Описание тестовой задачи", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30));
        task.setId(10);
        testTaskManager.addTask(task);
        //проверяем, что у задачи будет тот id, который генерируем сами, а не тот, который был у задачи изначально
        assertEquals(1, testTaskManager.getTask(1).getId());
    }

    @Test //проверяем, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    public void tasksAddedToHistoryManagerKeepThePreviousVersionOfTheTaskAndItsData() {
        TaskManager testTaskManager = new InMemoryTaskManager();
        //добавляем задачу и запоминаем её id
        int id = testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        //добавляем в историю просмотров путём получения задачи
        testTaskManager.getTask(id);
        //создаём новую задачу, которую позже передадим в качестве обновлённой
        Task updatedTask = new Task("Задача 1", "Новое описание задачи 1", Status.IN_PROGRESS,
                LocalDateTime.of(2020, 7, 2, 10, 0),
                Duration.ofMinutes(30));
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
        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        Task requestedTask = testTaskManager.getTask(1);
        requestedTask = testTaskManager.getTask(1);
        assertEquals(testTaskManager.getHistory().size(), 1);
    }

    @Test //проверяем, что история хранит обновленную инфу при двойном запрашивании
    public void historyStoresUpdatedInfoWhenATaskWasRequestedAfterChanging() {
        TaskManager testTaskManager = new InMemoryTaskManager();
        //добавляем задачу и запоминаем её id
        int id = testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        //добавляем в историю просмотров путём получения задачи
        testTaskManager.getTask(id);
        //создаём новую задачу, которую позже передадим в качестве обновлённой
        Task updatedTask = new Task("Задача 1", "Новое описание задачи 1", Status.IN_PROGRESS,
                LocalDateTime.of(2020, 7, 2, 10, 0),
                Duration.ofMinutes(30));
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
        testTaskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        testTaskManager.addTask(new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2020, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
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
        testTaskManager.addSubtask(new Subtask(1,"Подзадача эпика 1", "Описание подзадачи эпика 1",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        testTaskManager.deleteSubtask(2);
        assertEquals(testTaskManager.getEpic(1).getSubtaskIds().size(), 0);
    }

    @Test //При удалении эпика из истории удаляются и его подзадачи
    public void whenEpicIsDeletedFromHistoryItsSubtasksAreAlsoDeleted() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1,"Закончить 4й спринт", "Сдать финальное задание 6го спринта",
                Status.NEW, LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.getEpic(1);
        taskManager.getSubtask(2);
        assertEquals(taskManager.getHistory().size(), 2);
        taskManager.deleteEpic(1);
        assertEquals(taskManager.getHistory().size(), 0);
    }

    @Test //при удалении всех обычных задач, они корректно удаляются из истории
    public void whenTasksAreDeletedHistoryIsAlsoClearedCorrectly() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask(new Task("Трекер задач", "Написать программу трекер-задач для четвертого спринта", Status.IN_PROGRESS,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30))); //id-1
        taskManager.addTask(new Task("Купить продукты", "1.Молоко, 2.Хлеб, 3. Печенье", Status.NEW,
                LocalDateTime.of(2020, 7, 2, 10, 0),
                Duration.ofMinutes(30))); //id-2
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
        taskManager.addSubtask(new Subtask(1, "Закончить 4й спринт", "Сдать финальное задание 4го спринта",
                Status.NEW, LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 5й спринт", "Сделать финальное задание 5го спринта",
                Status.NEW, LocalDateTime.of(2022, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
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
        taskManager.addSubtask(new Subtask(1, "Закончить 4й спринт", "Сдать финальное задание 4го спринта",
                Status.NEW, LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 5й спринт", "Сделать финальное задание 5го спринта",
                Status.NEW, LocalDateTime.of(2022, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
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
        taskManager.addTask(new Task("Трекер задач", "Написать программу трекер-задач для четвертого спринта", Status.IN_PROGRESS,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addTask(new Task("Купить продукты", "1.Молоко, 2.Хлеб, 3. Печенье", Status.NEW,
                LocalDateTime.of(2020, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
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
        taskManager.addSubtask(new Subtask(1,"Закончить 4й спринт", "Сдать финальное задание 4го спринта",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 5й спринт", "Сделать финальное задание 5го спринта",
                Status.NEW, LocalDateTime.of(2022, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 6й спринт", "Сделать финальное задание 6го спринта",
                Status.NEW, LocalDateTime.of(2021, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        assertEquals(taskManager.getHistory().size(), 0);
        taskManager.getAllSubtasks();
        assertEquals(taskManager.getHistory().size(), 3);
    }

    //Проверяем расчеты статуса Эпика
    @Test
    public void checkUpdatingEpicStatusWhenSubtasksAreNew() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1,"Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.NEW,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 9й спринт", "Сделать финальное задание 5го спринта",
                Status.NEW, LocalDateTime.of(2024, 7, 22, 10, 0),
                Duration.ofMinutes(30)));
        assertEquals(taskManager.getEpic(1).getStatus(), Status.NEW);
    }

    @Test
    public void checkUpdatingEpicStatusWhenSubtasksAreDone() {
        TaskManager taskManager1 = new InMemoryTaskManager();
        taskManager1.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager1.addSubtask(new Subtask(1,"Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.DONE,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        taskManager1.addSubtask(new Subtask(1, "Закончить 9й спринт", "Сделать финальное задание 5го спринта",
                Status.DONE, LocalDateTime.of(2024, 7, 22, 10, 0),
                Duration.ofMinutes(30)));
        assertEquals(taskManager1.getEpic(1).getStatus(), Status.DONE);
    }

    @Test
    public void checkUpdatingEpicStatusWhenSubtasksAreNewAndDone() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1,"Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.DONE,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 9й спринт", "Сделать финальное задание 5го спринта",
                Status.NEW, LocalDateTime.of(2024, 7, 22, 10, 0),
                Duration.ofMinutes(30)));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(1).getStatus());
    }

    @Test
    public void checkUpdatingEpicStatusWhenSubtasksAreInProgress() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1,"Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 9й спринт", "Сделать финальное задание 5го спринта",
                Status.IN_PROGRESS, LocalDateTime.of(2024, 7, 22, 10, 0),
                Duration.ofMinutes(30)));
        assertEquals(taskManager.getEpic(1).getStatus(), Status.IN_PROGRESS);
    }

    //Проверяем, что подзадачи не могут существовать без соответствующего эпика, выбрасывается исключение
    @Test
    public void checkingAddingSubtasksWhenEpicDoesntExist() {
        TaskManager taskManager = new InMemoryTaskManager();
        assertThrows(TaskValidationException.class, () -> {
        taskManager.addSubtask(new Subtask(1,"Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));

        }, "Epic c id = 1 не найден");
    }
}