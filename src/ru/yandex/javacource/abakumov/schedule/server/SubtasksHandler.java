package ru.yandex.javacource.abakumov.schedule.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.abakumov.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.abakumov.schedule.managers.TaskManager;
import ru.yandex.javacource.abakumov.schedule.tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 3) {
            int id = Integer.parseInt(path[2]);
            if (method.equals("DELETE")) {
                deleteSubtask(id, exchange);
            } else {
                getSubtask(id, exchange);
            }
        } else if (method.equals("POST")) {
            final Subtask subtask = parseSubtask(exchange);
            postSubtask(subtask, exchange);
        } else {
            getAllSubtasks(exchange);
        }
    }

    private void getAllSubtasks(HttpExchange exchange) {
        final List<Subtask> allSubtasks = taskManager.getAllSubtasks();
        if (allSubtasks == null) {
            sendNotFound(exchange);
        }
        String jsonAllSubtasks = gson.toJson(allSubtasks);
        sendText(exchange, jsonAllSubtasks, 200);
    }

    private void getSubtask(int id, HttpExchange exchange) {
        final Subtask subtask = taskManager.getSubtask(id);
        if (subtask == null) {
            sendNotFound(exchange);
        }
        String jsonSubtask = gson.toJson(subtask);
        sendText(exchange, jsonSubtask, 200);
    }

    private void postSubtask(Subtask subtask, HttpExchange exchange) throws IOException {
        try {
            String answer;
            if (taskManager.getSubtask(subtask.getId()) != null) {
                taskManager.updateTask(subtask);
                answer = "Подзадача с номером " + subtask.getId() + " обновлена";
            } else {
                int id = taskManager.addSubtask(subtask);
                answer = "Подзадача с номером " + id + " добавлена";
            }
            sendText(exchange, gson.toJson(answer), 201);
        } catch (TaskValidationException e) {
            sendHasInteractions(exchange);
        }
    }

    private void deleteSubtask(int id, HttpExchange exchange) {
        taskManager.deleteSubtask(id);
        sendText(exchange, gson.toJson("Подзадача c номером " + id + " удалена"), 200);
    }

    private Subtask parseSubtask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Subtask subtask = gson.fromJson(body, Subtask.class);

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        if (jsonObject.has("id")) {
            int id = jsonObject.get("id").getAsInt();
            subtask.setId(id);
        }
        return subtask;
    }
}
