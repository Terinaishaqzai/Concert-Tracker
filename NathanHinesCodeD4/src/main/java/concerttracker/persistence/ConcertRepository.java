package concerttracker.persistence;

import concerttracker.model.Concert;
import java.util.List;

/** Reads concerts from storage; the service depends on this, not on SQLite. */
public interface ConcertRepository {

    /** Returns every concert in storage. */
    List<Concert> findAll();
}
