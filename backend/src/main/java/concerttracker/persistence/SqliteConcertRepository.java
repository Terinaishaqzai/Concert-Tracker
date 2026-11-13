package concerttracker.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import concerttracker.model.Concert;

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
                concerts.add(toConcert(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read concerts from the database", e);
        }
        return concerts;
    }

    /** Returns the concert with this id, or empty if no row matches. */
    @Override
    public Optional<Concert> findById(int id) {
        String sql = "SELECT * FROM concerts WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(toConcert(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read concert " + id + " from the database", e);
        }
    }

    /** Turns the current database row into a Concert. */
    private Concert toConcert(ResultSet rs) throws SQLException {
        return new Concert(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("genre"),
                rs.getString("venue"),
                rs.getString("city"),
                rs.getString("zip_code"),
                LocalDate.parse(rs.getString("date")),
                rs.getString("price_range"),
                rs.getString("seat_rating"),
                rs.getString("ticket_url"));
    }
}
