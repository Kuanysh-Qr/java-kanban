package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import typesoftasks.managers.NotFoundException;
import typesoftasks.managers.TaskManager;
import typesoftasks.tasks.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET" -> handleGet(exchange, query);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange, query);
                default -> exchange.sendResponseHeaders(405, 0); // Method Not Allowed
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0); // Internal Server Error
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            Collection<Epic> epics = manager.getAllEpics();
            sendText(exchange, gson.toJson(epics));
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Epic epic = manager.getEpic(id);
            sendText(exchange, gson.toJson(epic));
        } else {
            exchange.sendResponseHeaders(400, 0); // Bad Request
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() != 0 && manager.getEpic(epic.getId()) != null) {
            manager.updateEpic(epic);
            exchange.sendResponseHeaders(200, 0);
        } else {
            manager.createEpic(epic.getTitle(), epic.getDescription());
            exchange.sendResponseHeaders(201, 0);
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.deleteEpicById(id);
            exchange.sendResponseHeaders(204, 0);
        } else {
            exchange.sendResponseHeaders(400, 0); // Bad Request
        }
    }
}
