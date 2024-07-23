package ru.yandex.javacource.abakumov.schedule.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.abakumov.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.abakumov.schedule.managers.TaskManager;
import ru.yandex.javacource.abakumov.schedule.tasks.Task;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 3) {
            int id = Integer.parseInt(path[2]);
            if (method.equals("DELETE")) {
                deleteTask(id, exchange);
            } else {
                getTask(id, exchange);
            }
        } else if (method.equals("POST")) {
            final Task task = parseTask(exchange);
            postTask(task, exchange);
        } else {
            getAllTasks(exchange);
        }
    }

    //добавление задачи
    private void postTask(Task task, HttpExchange exchange) {
        try {
            String answer;
            if (taskManager.getTask(task.getId()) != null) {
                taskManager.updateTask(task);
                answer = "Задача с номером " + task.getId() + " обновлена";
            } else {
                int id = taskManager.addTask(task);
                answer = "Задача с номером " + id + " добавлена";
            }
            sendText(exchange, gson.toJson(answer), 201);
        } catch (TaskValidationException e) {
            sendHasInteractions(exchange);
        }
    }

    private void deleteTask(int id, HttpExchange exchange) {
        taskManager.deleteTask(id);
        sendText(exchange, gson.toJson("Задача c номером " + id + " удалена"), 200);
    }

    //получаем одну задачу
    private void getTask(int id, HttpExchange exchange) {
        final Task task = taskManager.getTask(id);
        if (task == null) {
            sendNotFound(exchange);
        }
        String jsonTask = gson.toJson(task);
        sendText(exchange, jsonTask, 200);
    }

    //получаем все задачи
    private void getAllTasks(HttpExchange exchange) {
        final List<Task> allTasks = taskManager.getAllTasks();
        if (allTasks == null) {
            sendNotFound(exchange);
        }
        String jsonAllTasks = gson.toJson(allTasks);
        sendText(exchange, jsonAllTasks, 200);
    }

    //Десериализуем задачу
    private Task parseTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Task task = gson.fromJson(body, Task.class);

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        if (jsonObject.has("id")) {
            int id = jsonObject.get("id").getAsInt();
            task.setId(id);
        }
        return task;
    }
}
