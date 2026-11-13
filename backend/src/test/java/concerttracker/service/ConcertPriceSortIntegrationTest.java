package concerttracker.service;

import static org.junit.jupiter.api.Assertions.*;
import com.sun.net.httpserver.HttpServer;
import concerttracker.controller.ConcertPriceSortController;
import concerttracker.persistence.ConcertRepository;
import concerttracker.persistence.SqliteConcertRepository;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Uses real HTTP requests and a temporary SQLite database, not shared test copies. */
class ConcertPriceSortIntegrationTest {
    @TempDir Path folder;
    private HttpServer server;
    private String baseUrl;
    private ConcertRepository repository;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void startServer() throws Exception {
        String dbUrl = "jdbc:sqlite:" + folder.resolve("concerts.db");
        try (var connection = DriverManager.getConnection(dbUrl);
             var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE concerts (id INTEGER, name TEXT, genre TEXT, venue TEXT, city TEXT, date TEXT, price_range TEXT, seat_rating TEXT, ticket_url TEXT)");
            String today = LocalDate.now().toString();
            String yesterday = LocalDate.now().minusDays(1).toString();
            statement.execute("INSERT INTO concerts VALUES "
                + "(1, 'Rock Expensive', 'Rock', 'Arena', 'Test City', '" + today + "', '$80-500', 'Great', 'https://example.com'),"
                + "(2, 'Rock Cheap', 'Rock', 'Arena', 'Test City', '" + today + "', '$10-1000', 'Great', 'https://example.com'),"
                + "(3, 'Rock Missing', 'Rock', 'Arena', 'Test City', '" + today + "', NULL, 'Great', 'https://example.com'),"
                + "(4, 'Jazz Show', 'Jazz', 'Arena', 'Test City', '" + today + "', '$25', 'Great', 'https://example.com'),"
                + "(5, 'Past Show', 'Rock', 'Arena', 'Test City', '" + yesterday + "', '$1', 'Great', 'https://example.com')");
            try (var insert = connection.prepareStatement("INSERT INTO concerts VALUES (6, ?, 'Rock', 'Arena', 'Other City', ?, '$40', 'Great', 'https://example.com')")) {
                insert.setString(1, "Rock \"Quoted\"\nShow");
                insert.setString(2, today);
                insert.executeUpdate();
            }
        }
        repository = new SqliteConcertRepository(dbUrl);
        var controller = new ConcertPriceSortController(new ConcertSearchService(repository), new ConcertPriceSortService(repository));
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/concerts/sort", controller::handleSort);
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/api/concerts/sort";
    }

    @AfterEach
    void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    void ascendingUsesRangeMinimumAndKeepsMissingLast() throws Exception {
        var response = get("?order=asc&q=Test+City");
        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("application/json"));
        assertOrder(response.body(), "Rock Cheap", "Jazz Show", "Rock Expensive", "Rock Missing");
        assertFalse(response.body().contains("Past Show"));
        assertTrue(response.body().contains("Price unavailable"));
    }

    @Test
    void descendingPreservesSearchAndMissingLast() throws Exception {
        String body = get("?order=desc&q=Rock").body();
        assertOrder(body, "Rock Expensive", "Quoted", "Rock Cheap", "Rock Missing");
        assertFalse(body.contains("Jazz Show"));
        assertFalse(body.contains("Past Show"));
    }

    @Test
    void defaultOrderIsAscendingAndNoMatchIsEmpty() throws Exception {
        assertEquals(get("?order=asc").body(), get("").body());
        assertEquals("[]", get("?order=desc&q=nothing-matches").body());
    }

    @Test
    void rejectsInvalidDirectionAndUnsupportedMethod() throws Exception {
        assertEquals(400, get("?order=wrong").statusCode());
        assertEquals(400, get("?order=").statusCode());
    }

    @Test
    void postIsNotAllowed() throws Exception {
        var request = HttpRequest.newBuilder(URI.create(baseUrl)).POST(HttpRequest.BodyPublishers.noBody()).build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("GET", response.headers().firstValue("Allow").orElse(""));
    }

    @Test
    void jsonEscapesQuotesAndNewlines() throws Exception {
        String body = get("?q=Quoted").body();
        assertTrue(body.contains("\\\"Quoted\\\"\\u000aShow"));
        assertFalse(body.contains("\n"));
    }

    @Test
    void sortingSearchResultsDoesNotReadRepositoryAgain() {
        int[] calls = {0};
        ConcertRepository countingRepo = () -> { calls[0]++; return repository.findAll(); };
        var search = new ConcertSearchService(countingRepo);
        var sort = new ConcertPriceSortService(countingRepo);
        var results = search.search("rock");
        var sorted = sort.sortByPriceDescending(results);
        assertEquals(1, calls[0]);
        assertNotSame(results, sorted);
        assertEquals(4, sorted.size());
    }

    private HttpResponse<String> get(String query) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(baseUrl + query)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private void assertOrder(String body, String... names) {
        int previous = -1;
        for (String name : names) {
            int index = body.indexOf(name);
            assertTrue(index > previous, name + " is missing or out of order: " + body);
            previous = index;
        }
    }
}
