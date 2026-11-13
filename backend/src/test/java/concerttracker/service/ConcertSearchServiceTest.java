package concerttracker.service;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import concerttracker.model.Concert;
import concerttracker.persistence.ConcertRepository;

/** Tests the search rules using a fake repository instead of the real database. */
class ConcertSearchServiceTest {
    private static final LocalDate TODAY = LocalDate.now();

    private final List<Concert> data = List.of(
            new Concert(1, "Drake Live", "Hip-Hop", "Capital One Arena", "Washington, DC","03134",
                    TODAY.plusDays(30), "$120-450", "Great", "https://www.ticketmaster.com"),
            new Concert(2, "Concert 1", "Rock", "MSG", "New York, NY","09325",
                    TODAY.plusDays(10), null, "Average", "https://www.ticketmaster.com"),
            new Concert(3, "Old Show", "Pop", "MSG", "New York, NY","09325",
                    TODAY.minusDays(5), "$20-60", "Great", "https://www.seatgeek.com"));

    // Fake repository: works because the service depends on the interface
    private final ConcertRepository fakeRepo = () -> data;
    private final ConcertSearchService service = new ConcertSearchService(fakeRepo);

    @Test
    void matchesArtistIgnoringCase() {
        List<Concert> results = service.search("DRAKE");
        assertEquals(1, results.size());
        assertEquals("Drake Live", results.get(0).getName());
    }

    @Test
    void matchesGenreVenueAndCity() {
        assertEquals(1, service.search("hip-hop").size());
        assertEquals(1, service.search("msg").size());
        assertEquals(1, service.search("washington").size());
    }

    @Test
    void blankQueryReturnsAllUpcomingSoonestFirst() {
        List<Concert> results = service.search("   ");
        assertEquals(2, results.size());
        assertEquals("Concert 1", results.get(0).getName());
        assertEquals(2, service.search(null).size());
    }

    @Test
    void pastConcertsAreHidden() {
        assertTrue(service.search("old show").isEmpty());
    }

    @Test
    void missingPriceIsLabeled() {
        Concert c = service.search("concert 1").get(0);
        assertEquals(ConcertSearchService.PRICE_UNAVAILABLE, c.getPriceRange());
    }

    @Test
    void noMatchReturnsEmptyList() {
        assertTrue(service.search("taylor swift").isEmpty());
    }
}
