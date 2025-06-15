package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import typesoftasks.json.DurationAdapter;
import typesoftasks.managers.NotFoundException;
import typesoftasks.managers.TaskManager;
import typesoftasks.tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();


    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
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
                default -> exchange.sendResponseHeaders(405, 0);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            Collection<Subtask> subtasks = manager.getAllSubtasks();
            sendText(exchange, gson.toJson(subtasks));
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Subtask subtask = manager.getSubtaskById(id);
            sendText(exchange, gson.toJson(subtask));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask.getId() != 0 && manager.getSubtaskById(subtask.getId()) != null) {
            manager.updateSubtask(subtask);
            exchange.sendResponseHeaders(200, 0);
        } else {
            manager.createSubtask(subtask.getTitle(), subtask.getDescription(), subtask.getEpicId());
            exchange.sendResponseHeaders(201, 0);
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.deleteSubtaskById(id);
            exchange.sendResponseHeaders(204, 0);
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
    }
}
