package ru.yandex.javacource.abakumov.schedule.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.abakumov.schedule.server.*;
import ru.yandex.javacource.abakumov.schedule.server.adapters.DurationAdapter;
import ru.yandex.javacource.abakumov.schedule.server.adapters.LocalDateTimeAdapter;
import ru.yandex.javacource.abakumov.schedule.tasks.Epic;
import ru.yandex.javacource.abakumov.schedule.tasks.Status;
import ru.yandex.javacource.abakumov.schedule.tasks.Subtask;
import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager, 8080);
    HttpClient client = HttpClient.newHttpClient();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }


    @Test
    public void getPrioritized() {
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
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код ответа от сервера: " + response.statusCode());
            assertEquals(200, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getHistory() {
        taskManager.addTask(new Task("Тестовая задача для истории",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addTask(new Task("Тестовая задача 2 для новой истории",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2022, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.getTask(1);
        taskManager.getTask(2);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/history"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код ответа от сервера: " + response.statusCode());
            System.out.println("Тело ответа: " + response.body());
            //Нужный статус
            assertEquals(200, response.statusCode());
            //Задачи добавились в историю
            assertEquals(2, taskManager.getHistory().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //Проверка задач

    @Test
    public void postNewTask() {
        Task task = new Task("Тестовая задача по добавлению задачи",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(1, taskManager.getAllTasks().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void postUpdateTask() {
        taskManager.addTask(new Task("Тестовая задача",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        Task task = new Task(1, "Тестовая задача для замены",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 12, 10, 0),
                Duration.ofMinutes(30));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(1, taskManager.getAllTasks().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void postOverlappingTask() {
        taskManager.addTask(new Task("Тестовая задача",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        Task task = new Task("Тестовая задача для замены",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(406, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTask() {
        taskManager.addTask(new Task("Тестовая задача для удаления задачи",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        assertEquals(1, taskManager.getAllTasks().size());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(0, taskManager.getAllTasks().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTask() {
        taskManager.addTask(new Task("Тестовая задача для истории",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(200, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNotExistingTask() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(404, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllTasks() {
        taskManager.addTask(new Task("Тестовая задача для истории",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2023, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addTask(new Task("Тестовая задача 2 для новой истории",
                "Какое-то описание",
                Status.NEW,
                LocalDateTime.of(2022, 7, 2, 10, 0),
                Duration.ofMinutes(30)));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(200, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //Проверка подзадач

    @Test
    public void postNewSubtask() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        Subtask subtask = new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(1, taskManager.getAllSubtasks().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void postOverlappingSubtask() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        Subtask subtask = new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(406, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void postUpdateSubtask() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        Subtask subtask = new Subtask(2, "Подзадача для замены", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS, 1,
                LocalDateTime.of(2024, 7, 12, 10, 0),
                Duration.ofMinutes(30));
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(1, taskManager.getAllSubtasks().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteSubtask() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        assertEquals(1, taskManager.getAllSubtasks().size());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(0, taskManager.getAllSubtasks().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSubtask() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/2"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(200, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNotExistingSubtask() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(404, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllSubtasks() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 9й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2026, 7, 22, 10, 0),
                Duration.ofMinutes(30)));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(200, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //Проверка эпиков

    @Test
    public void getAllEpics() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addEpic(new Epic("Второй эпик", "Описание второго эпика"));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(200, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpic() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(200, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNotExistingEpic() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(404, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEpicSubtasks() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        taskManager.addSubtask(new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 10, 10, 0),
                Duration.ofMinutes(30)));
        taskManager.addSubtask(new Subtask(1, "Закончить 8й спринт", "Сдать финальное задание 4го спринта",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 7, 12, 10, 0),
                Duration.ofMinutes(30)));
        assertEquals(2, taskManager.getAllSubtasks().size());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1/subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(200, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNotExistingEpicSubtasks() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        assertEquals(1, taskManager.getAllEpics().size());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/1/subtasks"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(404, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void postNewEpic() {
        Epic epic = new Epic("Закончить курс практикума", "Пройти все разделы");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(URI.create("http://localhost:8080/epics"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(1, taskManager.getAllEpics().size());
            assertEquals(201, response.statusCode());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void postUpdateEpic() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        Epic epic = new Epic(1, "Закончить курс практикума", "Пройти все разделы");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(URI.create("http://localhost:8080/epics"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(1, taskManager.getAllEpics().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteEpic() {
        taskManager.addEpic(new Epic("Закончить курс практикума", "Пройти все разделы"));
        assertEquals(1, taskManager.getAllEpics().size());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/epics/1"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            assertEquals(0, taskManager.getAllEpics().size());
        } catch (IllegalArgumentException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
