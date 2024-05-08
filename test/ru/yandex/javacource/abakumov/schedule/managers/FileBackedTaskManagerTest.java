package ru.yandex.javacource.abakumov.schedule.managers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.abakumov.schedule.tasks.Epic;
import ru.yandex.javacource.abakumov.schedule.tasks.Status;
import ru.yandex.javacource.abakumov.schedule.tasks.Subtask;
import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManagerTest {

    private final String pathToFile = "resources\\history.csv";

    @Test //проверяем, что файл по указанному пути существует
    public void theFileInTheSpecifiedPathExists() {
        Path file = Paths.get(pathToFile);
        assertTrue(Files.exists(file));
    }

    @Test //проверяем, что информация успешно записывается в файл
    public void theInformationIsSuccessfullyWrittenToTheFile() {
        String checkLine = "Hello";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile, false))) {
            writer.write(checkLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
            String actualLine = reader.readLine();
            assertEquals(actualLine, checkLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test //проверяем запись в файл с помощью метода save
    public void checkingTheOperationOfTheSaveMethod() {
        TaskManager taskManager = new FileBackedTaskManager(new File(pathToFile));
        taskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW));
        taskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.addSubtask(new Subtask(2,"Подзадача эпика 1", "Описание подзадачи эпика 1", Status.NEW));
        taskManager.addTask(new Task("Задача 2", "Описание задачи 2", Status.NEW));
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(lineCount, 5); //в файле должно быть 5 строк
    }

    @Test //проверяем наполнение пустого класса FileBackedTaskManager из файла
    public void checkFileBackedTaskManagerCreatingFromFile() {
        TaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(new File(pathToFile));
        /*В подготовленном файле для тестов это класса четыре задачи: 2 обычных и эпик с подзадачей, как указано
        в предыдущем тестовом методе
        Проверим, добавились ли они в новый менеджер из файла*/
        assertEquals(taskManagerFromFile.getAllTasks().size(), 2);
        assertEquals(taskManagerFromFile.getAllEpics().size(), 1);
        assertEquals(taskManagerFromFile.getAllSubtasks().size(), 1);
    }
}