import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Backend: a tiny REST API built on the JDK's own HTTP server (no libraries).
 *   GET  /api/balance
 *   POST /api/deposit    {"amount": 500}
 *   POST /api/withdraw   {"amount": 200}
 * It also serves the front end from web/index.html.
 */
public class AtmServer {
    private static final Account account = new Account("Demo User", 1000.00);
    private static final Pattern AMOUNT = Pattern.compile("\"amount\"\\s*:\\s*\"?(-?[0-9.]+)\"?");

    @FunctionalInterface
    interface Action {
        String run(HttpExchange ex) throws Exception;
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", AtmServer::serveFrontend);
        server.createContext("/api/balance", ex -> handle(ex, "GET", e -> reply("Balance updated.")));
        server.createContext("/api/deposit", ex -> handle(ex, "POST", e -> {
            account.deposit(readAmount(e));
            return reply("Deposit successful.");
        }));
        server.createContext("/api/withdraw", ex -> handle(ex, "POST", e -> {
            account.withdraw(readAmount(e));
            return reply("Withdrawal successful. Please take your cash.");
        }));
        server.start();
        System.out.println("ATM running at http://localhost:8080");
    }

    /** CONCEPT 3 - EXCEPTION HANDLING: every failure becomes a clear HTTP response. */
    private static void handle(HttpExchange ex, String method, Action action) throws IOException {
        if (!ex.getRequestMethod().equals(method)) {
            send(ex, 405, error("Method not allowed."));
            return;
        }
        try {
            send(ex, 200, action.run(ex));
        } catch (InvalidAmountException e) {
            send(ex, 400, error(e.getMessage()));
        } catch (InsufficientFundsException e) {
            send(ex, 409, error(e.getMessage()));
        } catch (Exception e) {
            send(ex, 500, error("Something went wrong. Please try again."));
        }
    }

    private static double readAmount(HttpExchange ex) throws IOException, InvalidAmountException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Matcher m = AMOUNT.matcher(body);
        if (!m.find()) {
            throw new InvalidAmountException("Enter an amount.");
        }
        try {
            return Double.parseDouble(m.group(1));
        } catch (NumberFormatException e) {
            throw new InvalidAmountException("That is not a valid amount.");
        }
    }

    private static String reply(String message) {
        return String.format("{\"holder\":\"%s\",\"balance\":%.2f,\"message\":\"%s\"}",
                account.getHolderName(), account.getBalance(), message);
    }

    private static String error(String message) {
        return "{\"error\":\"" + message.replace("\"", "'") + "\"}";
    }

    private static void serveFrontend(HttpExchange ex) throws IOException {
        Path page = Path.of("web", "index.html");
        if (!ex.getRequestURI().getPath().equals("/") || !Files.exists(page)) {
            send(ex, 404, error("Not found."));
            return;
        }
        byte[] bytes = Files.readAllBytes(page);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void send(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
