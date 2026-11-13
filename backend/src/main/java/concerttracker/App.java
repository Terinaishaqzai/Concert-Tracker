package concerttracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.net.httpserver.HttpServer;

import concerttracker.controller.ConcertDetailsController;
import concerttracker.controller.ConcertPriceSortController;
import concerttracker.controller.ConcertSearchController;
import concerttracker.controller.ConcertZipController;
import concerttracker.persistence.ConcertRepository;
import concerttracker.persistence.SqliteConcertRepository;
import concerttracker.service.ConcertDetailsService;
import concerttracker.service.ConcertPriceSortService;
import concerttracker.service.ConcertSearchService;
import concerttracker.service.ConcertZipService;

/** Starts the Concert Tracker server and connects the three layers. */
public class App {
    private static final String DB_URL = "jdbc:sqlite:concerts.db";
    private static final int PORT = 7070;

    /** Entry point: set up the database, wire the layers, and start the server. */
    public static void main(String[] args) throws Exception {
        setUpDatabase();

        // Persistence -> Business logic -> Controller (dependencies point down)
        ConcertRepository repo = new SqliteConcertRepository(DB_URL);
        ConcertSearchService searchService = new ConcertSearchService(repo);
        ConcertSearchController searchController = new ConcertSearchController(searchService);
        ConcertDetailsService detailsService = new ConcertDetailsService(repo);
        ConcertDetailsController detailsController = new ConcertDetailsController(detailsService);

        ConcertPriceSortService sortService = new ConcertPriceSortService(repo);
        ConcertPriceSortController sortController = new ConcertPriceSortController(searchService, sortService);

        ConcertZipService zipServ = new ConcertZipService(searchService);
        ConcertZipController zipCont = new ConcertZipController(zipServ);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/concerts/search", searchController::handleSearch);
        server.createContext("/api/concerts/details", detailsController::handleDetails);
        server.createContext("/api/concerts/sort", sortController::handleSort);
        server.createContext("/api/concerts/zip",zipCont::handleZip);
        server.createContext("/", App::serveWebsiteFile);
        server.start();
        System.out.println("Concert Tracker running at http://localhost:" + PORT);
    }

    /** Creates the concerts table and sample data by running schema.sql. */
    private static void setUpDatabase() throws IOException, SQLException {
        try (InputStream in = App.class.getResourceAsStream("/schema.sql");
             Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String command : sql.split(";")) {
                if (!command.isBlank()) {
                    stmt.execute(command);
                }
            }
        }
    }

    /** Sends index.html, styles.css, etc. from the concerttracker.com folder. */
    private static void serveWebsiteFile(com.sun.net.httpserver.HttpExchange exchange)
            throws IOException {
        Path folder = findWebsiteFolder();
        String name = exchange.getRequestURI().getPath().equals("/")
                ? "index.html" : exchange.getRequestURI().getPath().substring(1);
        Path file = folder.resolve(name).normalize();

        if (!file.startsWith(folder) || !Files.isRegularFile(file)) {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
            return;
        }
        String type = name.endsWith(".css") ? "text/css" : name.endsWith(".png")
                ? "image/png" : "text/html; charset=utf-8";
        byte[] body = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", type);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    /** Finds the concerttracker.com folder whether run from the repo root or backend/. */
    private static Path findWebsiteFolder() {
        Path fromRoot = Path.of("concerttracker.com");
        Path folder = Files.isDirectory(fromRoot) ? fromRoot : Path.of("..", "concerttracker.com");
        return folder.toAbsolutePath().normalize();
    }
}
