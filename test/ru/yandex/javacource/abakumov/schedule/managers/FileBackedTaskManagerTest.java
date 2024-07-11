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
import java.time.Duration;
import java.time.LocalDateTime;

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
        taskManager.addTask(new Task("Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2019, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.addSubtask(new Subtask(2,"Подзадача эпика 1", "Описание подзадачи эпика 1",
                Status.NEW, LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addTask(new Task("Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2020, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
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

    //проверяем, что исключение не выбрасывается в случае, если файл существует
    @Test
    public void testFileExists() {
        assertDoesNotThrow(() -> {
            File file = new File(pathToFile);
            if (!file.exists()) {
                throw new IOException("Файл существует");
            }
        }, "Файл существует");
    }

    //проверяем, что выбрасывается исключение, если передаем ссылку на несуществующий файл
    @Test
    public void testFileDoesNotExist() {
        assertThrows(IOException.class, () -> {
            File file = new File("resources\\non_existent_file.csv");
            if (!file.exists()) {
                throw new IOException("Файл не существует");
            }
        }, "Файл не существует");
    }
}