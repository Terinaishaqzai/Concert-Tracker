package concerttracker.controller;

import com.sun.net.httpserver.HttpExchange;
import concerttracker.model.Concert;
import concerttracker.service.ConcertSearchService;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** Receives search requests from the web page and returns results as JSON. */
public class ConcertSearchController {
    private final ConcertSearchService service;

    /** Creates the controller with the service that holds the search rules. */
    public ConcertSearchController(ConcertSearchService service) {
        this.service = service;
    }

    /** Handles GET /api/concerts/search?q=... */
    public void handleSearch(HttpExchange exchange) throws IOException {
        String query = readQueryParam(exchange, "q");
        List<Concert> results = service.search(query);
        byte[] body = toJson(results).getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    /** Returns the value of a ?name=value parameter, or null if it is missing. */
    private String readQueryParam(HttpExchange exchange, String name) {
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery == null) {
            return null;
        }
        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts[0].equals(name)) {
                return parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            }
        }
        return null;
    }

    /** Converts a list of concerts to a JSON array. */
    private String toJson(List<Concert> concerts) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < concerts.size(); i++) {
            Concert c = concerts.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{")
              .append("\"id\":").append(c.getId()).append(",")
              .append("\"name\":").append(quote(c.getName())).append(",")
              .append("\"genre\":").append(quote(c.getGenre())).append(",")
              .append("\"venue\":").append(quote(c.getVenue())).append(",")
              .append("\"city\":").append(quote(c.getCity())).append(",")
              .append("\"date\":").append(quote(c.getDate().toString())).append(",")
              .append("\"priceRange\":").append(quote(c.getPriceRange())).append(",")
              .append("\"seatRating\":").append(quote(c.getSeatRating())).append(",")
              .append("\"ticketUrl\":").append(quote(c.getTicketUrl()))
              .append("}");
        }
        return sb.append("]").toString();
    }

    /** Wraps text in quotes and escapes characters that would break JSON; null becomes null. */
    private String quote(String text) {
        if (text == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (char ch : text.toCharArray()) {
            if (ch == '"' || ch == '\\') {
                sb.append('\\').append(ch);
            } else if (ch < 32) {
                sb.append(String.format("\\u%04x", (int) ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.append('"').toString();
    }
}
