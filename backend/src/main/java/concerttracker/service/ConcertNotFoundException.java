package concerttracker.service;

/** Thrown when no concert has the requested id. */
public class ConcertNotFoundException extends RuntimeException {

    /** Creates the exception for the id that was not found. */
    public ConcertNotFoundException(int id) {
        super("Concert not found: " + id);
    }
}
