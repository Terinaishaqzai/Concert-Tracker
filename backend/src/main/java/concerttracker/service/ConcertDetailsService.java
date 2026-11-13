package concerttracker.service;

import concerttracker.model.Concert;
import concerttracker.model.ConcertDetails;
import concerttracker.persistence.ConcertRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Business logic for the Concert Details panel. */
public class ConcertDetailsService {
    private final ConcertRepository repo;

    /** Creates the service with the repository it reads concerts from. */
    public ConcertDetailsService(ConcertRepository repo) {
        this.repo = repo;
    }

    /** Returns the details for one concert, or throws if the id does not exist. */
    public ConcertDetails getDetails(int id) {
        Concert c = repo.findById(id).orElseThrow(() -> new ConcertNotFoundException(id));
        return new ConcertDetails(c, getStatus(c), daysUntil(c), priceLabel(c));
    }

    /** Rule 1: a concert before today has ended. */
    private String getStatus(Concert c) {
        return c.getDate().isBefore(LocalDate.now()) ? "Event Ended" : "Upcoming";
    }

    /** Rule 2: days until the show, never negative. */
    private long daysUntil(Concert c) {
        return Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), c.getDate()));
    }

    /** Rule 3: missing prices are shown as "Price unavailable" (same text as search). */
    private String priceLabel(Concert c) {
        String price = c.getPriceRange();
        return (price == null || price.isBlank()) ? "Price unavailable" : price;
    }
}
