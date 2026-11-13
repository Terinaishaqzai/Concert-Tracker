package concerttracker.controller;

import com.sun.net.httpserver.HttpExchange;
import concerttracker.model.Concert;
import concerttracker.model.ConcertDetails;
import concerttracker.service.ConcertDetailsService;
import concerttracker.service.ConcertNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/** Receives details requests from the web page and returns one concert as JSON. */
public class ConcertDetailsController {
    private final ConcertDetailsService service;

    /** Creates the controller with the service that holds the details rules. */
    public ConcertDetailsController(ConcertDetailsService service) {
        this.service = service;
    }

    /** Handles GET /api/concerts/details?id=... */
    public void handleDetails(HttpExchange exchange) throws IOException {
        Integer id = readId(exchange);
        if (id == null) {
            send(exchange, 400, "{\"error\":\"Invalid concert\"}");
            return;
        }
        try {
            send(exchange, 200, toJson(service.getDetails(id)));
        } catch (ConcertNotFoundException e) {
            send(exchange, 404, "{\"error\":\"Concert not found\"}");
        }
    }

    /** Returns the ?id= value as a number, or null if it is missing or not a number. */
    private Integer readId(HttpExchange exchange) {
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery == null) {
            return null;
        }
        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts[0].equals("id") && parts.length > 1) {
                try {
                    return Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /** Converts the details to a JSON object. */
    private String toJson(ConcertDetails d) {
        Concert c = d.getConcert();
        return "{"
                + "\"id\":" + c.getId() + ","
                + "\"name\":" + quote(c.getName()) + ","
                + "\"genre\":" + quote(c.getGenre()) + ","
                + "\"venue\":" + quote(c.getVenue()) + ","
                + "\"city\":" + quote(c.getCity()) + ","
                + "\"date\":" + quote(c.getDate().toString()) + ","
                + "\"seatRating\":" + quote(c.getSeatRating()) + ","
                + "\"ticketUrl\":" + quote(c.getTicketUrl()) + ","
                + "\"status\":" + quote(d.getStatus()) + ","
                + "\"daysUntil\":" + d.getDaysUntil() + ","
                + "\"priceLabel\":" + quote(d.getPriceLabel())
                + "}";
    }

    /** Sends a JSON response with the given status code. */
    private void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    /** Wraps text in quotes and escapes characters that would break JSON. */
    private String quote(String text) {
        if (text == null) {
            return "null";
        }
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
