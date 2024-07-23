package ru.yandex.javacource.abakumov.schedule.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.abakumov.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.abakumov.schedule.managers.TaskManager;
import ru.yandex.javacource.abakumov.schedule.tasks.Epic;
import ru.yandex.javacource.abakumov.schedule.tasks.Subtask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        int id;
        if (path.length == 4) {
            id = Integer.parseInt(path[2]);
            getEpicSubtasks(id, exchange);
        } else if (path.length == 3) {
            id = Integer.parseInt(path[2]);
            if (method.equals("DELETE")) {
                deleteEpic(id, exchange);
            } else {
                getEpic(id, exchange);
            }
        } else if (method.equals("POST")) {
            final Epic epic = parseEpic(exchange);
            postEpic(epic, exchange);
        } else {
            getAllEpics(exchange);
        }
    }

    private void postEpic(Epic epic, HttpExchange exchange) {
        try {
            String answer;
            if (taskManager.getEpic(epic.getId()) != null) {
                taskManager.updateEpic(epic);
                answer = "Эпик с номером " + epic.getId() + " обновлен";
            } else {
                int id = taskManager.addEpic(epic);
                answer = "Эпик с номером " + id + " добавлен";
            }
            sendText(exchange, gson.toJson(answer), 201);
        } catch (TaskValidationException e) {
            sendHasInteractions(exchange);
        }
    }

    private void deleteEpic(int id, HttpExchange exchange) {
        taskManager.deleteEpic(id);
        sendText(exchange, gson.toJson("Эпик c номером " + id + " удален"), 200);
    }

    private void getEpic(int id, HttpExchange exchange) {
        final Epic epic = taskManager.getEpic(id);
        if (epic == null) {
            sendNotFound(exchange);
        }
        String jsonEpic = gson.toJson(epic);
        sendText(exchange, jsonEpic, 200);
    }

    private void getEpicSubtasks(int id, HttpExchange exchange) {
        final Epic epic = taskManager.getEpic(id);
        if (epic == null) {
            sendNotFound(exchange);
        }
        List<Integer> subtasksId = epic.getSubtaskIds();
        if (subtasksId.isEmpty()) {
            sendNotFound(exchange);

        }
        List<Subtask> subtasks = new ArrayList<>();
        for (Integer sub : subtasksId) {
            subtasks.add(taskManager.getSubtask(sub));
        }
        String jsonSubtasks = gson.toJson(subtasks);
        sendText(exchange, jsonSubtasks, 200);
    }

    private void getAllEpics(HttpExchange exchange) {
        final List<Epic> allEpics = taskManager.getAllEpics();
        if (allEpics == null) {
            sendNotFound(exchange);
        }
        String jsonAllEpics = gson.toJson(allEpics);
        sendText(exchange, jsonAllEpics, 200);
    }

    private Epic parseEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Epic epic = gson.fromJson(body, Epic.class);

        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        if (jsonObject.has("id")) {
            int id = jsonObject.get("id").getAsInt();
            epic.setId(id);
        }
        return epic;
    }
}
