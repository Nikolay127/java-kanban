package ru.yandex.javacource.abakumov.schedule.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.abakumov.schedule.server.adapters.DurationAdapter;
import ru.yandex.javacource.abakumov.schedule.server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class BaseHttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    //Поле для хранения сообщений о кодах
    private static final Map<Integer, String> CODE_MESSAGES = new HashMap<>();
    protected static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();


    protected Map<Integer, String> getCodeMessages() {
        return CODE_MESSAGES;
    }

    //Сообщаем об успехе. Код 200 или 201, передаётся в метод
    protected void sendText(HttpExchange exchange, String responseText, int responseCode) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(responseCode, responseText.getBytes(DEFAULT_CHARSET).length);
            os.write(responseText.getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            e.getMessage();
        } finally {
            exchange.close();
        }
    }

    //Сообщаем, что задачи (эпика или подзадачи) нет. Код 404
    protected void sendNotFound(HttpExchange exchange) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(404, 0);
            os.write(CODE_MESSAGES.get(404).getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            e.getMessage();
        } finally {
            exchange.close();
        }
    }

    //Сообщаем, что задача (эпик или подзадача) пересекается с существующими. Код 406
    protected void sendHasInteractions(HttpExchange exchange) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(406, 0);
            os.write(CODE_MESSAGES.get(406).getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            e.getMessage();
        } finally {
            exchange.close();
        }
    }

    //Сообщаем об ошибке при обработке запроса. Код 500
    protected void sendInternalServerError(HttpExchange exchange) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(500, 0);
            os.write(CODE_MESSAGES.get(500).getBytes(DEFAULT_CHARSET));
        } catch (IOException e) {
            e.getMessage();
        } finally {
            exchange.close();
        }
    }

    public static void setCodeMessages() {
        BaseHttpHandler.CODE_MESSAGES.put(404, "Not Found");
        BaseHttpHandler.CODE_MESSAGES.put(406, "Not Acceptable");
        BaseHttpHandler.CODE_MESSAGES.put(500, "Internal Server Error");
    }
}
