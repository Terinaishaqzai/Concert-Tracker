package concerttracker.service;

import concerttracker.model.Concert;
import concerttracker.persistence.ConcertRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Business logic for searching upcoming concerts by artist, genre, or city. */
public class ConcertSearchService {
    public static final String PRICE_UNAVAILABLE = "Price unavailable";

    private final ConcertRepository repo;

    /** Creates the service with the repository it reads concerts from. */
    public ConcertSearchService(ConcertRepository repo) {
        this.repo = repo;
    }

    /** Returns upcoming concerts matching the query, soonest first; blank returns all upcoming. */
    public List<Concert> search(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        List<Concert> results = new ArrayList<>();

        for (Concert c : repo.findAll()) {
            // Rule 1: only upcoming concerts; Rule 2: a blank search matches everything
            if (isUpcoming(c) && (q.isEmpty() || matches(c, q))) {
                // Rule 3: missing prices are labeled "Price unavailable"
                results.add(withPriceLabel(c));
            }
        }

        // Rule 4: soonest concert first (an empty list means "No concerts found")
        results.sort(Comparator.comparing(Concert::getDate));
        return results;
    }

    /** Returns true if the concert is today or later. */
    private boolean isUpcoming(Concert c) {
        return !c.getDate().isBefore(LocalDate.now());
    }

    /** Returns true if the artist, genre, venue, or city contains q (case-insensitive). */
    private boolean matches(Concert c, String q) {
        return c.getName().toLowerCase().contains(q)
                || c.getGenre().toLowerCase().contains(q)
                || c.getVenue().toLowerCase().contains(q)
                || c.getCity().toLowerCase().contains(q);
    }

    /** Returns the concert, with "Price unavailable" if it has no price. */
    private Concert withPriceLabel(Concert c) {
        if (c.getPriceRange() != null && !c.getPriceRange().isBlank()) {
            return c;
        }
        return new Concert(c.getId(), c.getName(), c.getGenre(), c.getVenue(), c.getCity(),
                c.getDate(), PRICE_UNAVAILABLE, c.getSeatRating(), c.getTicketUrl());
    }
}
