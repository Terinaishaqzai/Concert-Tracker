package concerttracker.persistence;

import concerttracker.model.Concert;
import java.util.List;
import java.util.Optional;

/** Reads concerts from storage; the service depends on this, not on SQLite. */
public interface ConcertRepository {

    /** Returns every concert in storage. */
    List<Concert> findAll();

    /**
     * Returns the concert with this id, or empty if there is none.
     * Default version searches findAll() so test fakes (lambdas) still work;
     * SqliteConcertRepository overrides it with a WHERE id = ? query.
     */
    default Optional<Concert> findById(int id) {
        return findAll().stream().filter(c -> c.getId() == id).findFirst();
    }
}
