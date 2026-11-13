package concerttracker.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import concerttracker.model.Concert;
import concerttracker.service.ConcertZipService;

/** Receives ZIP searches from the web page and returns results as JSON. */
public class ConcertZipController {
    private final ConcertZipService service;

    public ConcertZipController(ConcertZipService service) {
        this.service = service;
    }

    /** Handles GET /api/concerts/zip?zip=22030 */
    public void handleZip(HttpExchange exchange) throws IOException {
        String rawQuery = exchange.getRequestURI().getQuery();
        String zip = (rawQuery != null && rawQuery.startsWith("zip=")) ? rawQuery.substring(4) : null;

        if (!service.isValidZip(zip)) {
            send(exchange, 400, "{\"error\":\"ZIP code must be 5 digits\"}");
            return;
        }
        send(exchange, 200, toJson(service.findNearZip(zip)));
    }

    private void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    /** Same JSON fields as the search endpoint, so the page can reuse renderResults. */
    private String toJson(List<Concert> concerts) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < concerts.size(); i++) {
            Concert c = concerts.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{\"id\":").append(c.getId())
              .append(",\"name\":").append(quote(c.getName()))
              .append(",\"genre\":").append(quote(c.getGenre()))
              .append(",\"venue\":").append(quote(c.getVenue()))
              .append(",\"city\":").append(quote(c.getCity()))
              .append(",\"zipCode\":").append(quote(c.getZipCode()))
              .append(",\"date\":").append(quote(c.getDate().toString()))
              .append(",\"priceRange\":").append(quote(c.getPriceRange()))
              .append(",\"seatRating\":").append(quote(c.getSeatRating()))
              .append(",\"ticketUrl\":").append(quote(c.getTicketUrl()))
              .append("}");
        }
        return sb.append("]").toString();
    }

    private String quote(String text) {
        if (text == null) {
            return "null";
        }
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}