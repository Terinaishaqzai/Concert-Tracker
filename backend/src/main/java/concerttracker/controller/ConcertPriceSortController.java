package concerttracker.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import concerttracker.model.Concert;
import concerttracker.service.ConcertPriceSortService;
import concerttracker.service.ConcertSearchService;

/** Receives price sorting requests and returns the sorted search results as JSON. */
public class ConcertPriceSortController {
    private final ConcertSearchService searchService;
    private final ConcertPriceSortService sortService;

    public ConcertPriceSortController(ConcertSearchService searchService, ConcertPriceSortService sortService) {
        this.searchService = searchService;
        this.sortService = sortService;
    }

    /** GET /api/concerts/sort?order=asc|desc&q=... */
    public void handleSort(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.getResponseHeaders().set("Allow", "GET");
            sendJson(exchange, 405, "{\"error\":\"Use GET\"}");
            return;
        }

        String order;
        String query;
        try {
            order = readQueryParam(exchange, "order");
            query = readQueryParam(exchange, "q");
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, "{\"error\":\"Invalid query encoding\"}");
            return;
        }
        if (order == null) {
            order = "asc";
        }
        if (!order.equals("asc") && !order.equals("desc")) {
            sendJson(exchange, 400, "{\"error\":\"Order must be asc or desc\"}");
            return;
        }

        // Keep Eilaf's search rules, including upcoming dates and missing-price labels.
        List<Concert> concerts = searchService.search(query);
        if (order.equals("asc")) {
            concerts = sortService.sortByPriceAscending(concerts);
        } else {
            concerts = sortService.sortByPriceDescending(concerts);
        }
        sendJson(exchange, 200, toJson(concerts));
    }

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

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    // Same JSON fields as the existing search endpoint, so both views can reuse them.
    private String toJson(List<Concert> concerts) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < concerts.size(); i++) {
            Concert c = concerts.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                .append("\"id\":").append(c.getId()).append(",")
                .append("\"name\":").append(quote(c.getName())).append(",")
                .append("\"genre\":").append(quote(c.getGenre())).append(",")
                .append("\"venue\":").append(quote(c.getVenue())).append(",")
                .append("\"city\":").append(quote(c.getCity())).append(",")
                .append("\"zipCode\":").append(quote(c.getZipCode())).append(",")
                .append("\"date\":").append(quote(c.getDate().toString())).append(",")
                .append("\"priceRange\":").append(quote(c.getPriceRange())).append(",")
                .append("\"seatRating\":").append(quote(c.getSeatRating())).append(",")
                .append("\"ticketUrl\":").append(quote(c.getTicketUrl())).append("}");
        }
        return json.append("]").toString();
    }

    private String quote(String text) {
        if (text == null) {
            return "null";
        }
        StringBuilder escaped = new StringBuilder("\"");
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"' || c == '\\') {
                escaped.append('\\').append(c);
            } else if (c < 32) {
                escaped.append(String.format("\\u%04x", (int) c));
            } else {
                escaped.append(c);
            }
        }
        return escaped.append('"').toString();
    }
}
