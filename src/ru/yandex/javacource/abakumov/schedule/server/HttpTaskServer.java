package ru.yandex.javacource.abakumov.schedule.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.abakumov.schedule.managers.InMemoryTaskManager;
import ru.yandex.javacource.abakumov.schedule.managers.TaskManager;
import ru.yandex.javacource.abakumov.schedule.server.adapters.DurationAdapter;
import ru.yandex.javacource.abakumov.schedule.server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final TaskManager taskManager;
    private final int port;
    private HttpServer httpServer;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    public HttpTaskServer(TaskManager taskManager, int port) {
        this.taskManager = taskManager;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new InMemoryTaskManager();
        new HttpTaskServer(taskManager, 8080).start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        createEachContext(httpServer, taskManager);
        BaseHttpHandler.setCodeMessages();
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        return gson;
    }

    //Метод для прописывания контекста сервера
    private void createEachContext(HttpServer httpServer, TaskManager taskManager) {
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }
}
