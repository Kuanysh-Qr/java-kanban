package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import typesoftasks.json.DurationAdapter;
import typesoftasks.managers.NotFoundException;
import typesoftasks.managers.TaskManager;
import typesoftasks.tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();


    public TasksHandler(TaskManager manager) {
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
            List<Task> tasks = manager.getAllTasks();
            sendText(exchange, gson.toJson(tasks));
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Task task = manager.getTask(id);
            sendText(exchange, gson.toJson(task));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() != 0 && manager.getTask(task.getId()) != null) {
            manager.updateTask(task);
            exchange.sendResponseHeaders(200, 0);
        } else {
            manager.createTask(task.getTitle(), task.getDescription());
            exchange.sendResponseHeaders(201, 0);
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.deleteTaskById(id);
            exchange.sendResponseHeaders(204, 0);
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
    }
}
