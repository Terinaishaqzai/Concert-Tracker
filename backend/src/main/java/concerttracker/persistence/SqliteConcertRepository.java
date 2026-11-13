package concerttracker.persistence;

import concerttracker.model.Concert;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Loads concerts from the SQLite database using JDBC. */
public class SqliteConcertRepository implements ConcertRepository {
    private final String dbUrl;

    /** Creates a repository for the given JDBC url, e.g. "jdbc:sqlite:concerts.db". */
    public SqliteConcertRepository(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /** Returns every row in the concerts table as a Concert. */
    @Override
    public List<Concert> findAll() {
        List<Concert> concerts = new ArrayList<>();
        String sql = "SELECT * FROM concerts";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                concerts.add(new Concert(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("genre"),
                        rs.getString("venue"),
                        rs.getString("city"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("price_range"),
                        rs.getString("seat_rating"),
                        rs.getString("ticket_url")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read concerts from the database", e);
        }
        return concerts;
    }
}
