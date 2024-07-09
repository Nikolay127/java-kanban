package ru.yandex.javacource.abakumov.schedule.managers;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.abakumov.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.abakumov.schedule.tasks.Status;
import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimeAndDurationTest {

    private final String pathToFile = "resources\\history.csv";

    //Проверяем, что новый функционал работает и записывает задачи в файл
    @Test
    public void сheckingInitialWorkOfNewFunctions() {
        TaskManager taskManager = new FileBackedTaskManager(new File(pathToFile));
        taskManager.addTask(new Task("Тестовая задача",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addTask(new Task("Тестовая задача2",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2022, 7, 2, 10, 0),
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
        assertEquals(lineCount, 3);
    }

    //проверяем, что в случае наслоения задача не записывается, выбрасывается исключение
    @Test
    public void isOverLapping() {
        TaskManager taskManager = new FileBackedTaskManager(new File(pathToFile));
        taskManager.addTask(new Task("Записанная задача",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofDays(30)));
        assertThrows(TaskValidationException.class, () -> {
            taskManager.addTask(new Task("Незаписанная задача",
                    "Какое-то описание",
                    Status.NEW,
                    LocalDateTime.of(2023, 7, 12, 10, 0),
                    Duration.ofMinutes(30)));
        }, "Задача пересекается с уже существующими");
    }
}
