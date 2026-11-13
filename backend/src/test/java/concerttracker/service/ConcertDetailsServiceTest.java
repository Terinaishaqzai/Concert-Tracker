package concerttracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import concerttracker.model.Concert;
import concerttracker.model.ConcertDetails;
import concerttracker.persistence.ConcertRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests the Concert Details rules using a fake repository instead of the real database. */
class ConcertDetailsServiceTest {
    private static final LocalDate TODAY = LocalDate.now();

    private final List<Concert> data = List.of(
            new Concert(1, "Drake Live", "Hip-Hop", "Capital One Arena", "Washington, DC",
                    TODAY.plusDays(30), "$120-450", "Great", "https://www.ticketmaster.com"),
            new Concert(2, "Concert 2", "Country", "Staples Center", "Los Angeles, CA",
                    TODAY.plusDays(10), null, "Great", "https://www.stubhub.com"),
            new Concert(3, "Old Show", "Pop", "MSG", "New York, NY",
                    TODAY.minusDays(5), "$20-60", "Great", "https://www.seatgeek.com"));

    // Fake repository: findById comes from the interface's default method
    private final ConcertRepository fakeRepo = () -> data;
    private final ConcertDetailsService service = new ConcertDetailsService(fakeRepo);

    @Test
    void upcomingConcertHasStatusDaysAndPrice() {
        ConcertDetails d = service.getDetails(1);
        assertEquals("Drake Live", d.getConcert().getName());
        assertEquals("Upcoming", d.getStatus());
        assertEquals(30, d.getDaysUntil());
        assertEquals("$120-450", d.getPriceLabel());
    }

    @Test
    void pastConcertIsEventEnded() {
        ConcertDetails d = service.getDetails(3);
        assertEquals("Event Ended", d.getStatus());
        assertEquals(0, d.getDaysUntil());
    }

    @Test
    void missingPriceIsLabeled() {
        assertEquals("Price unavailable", service.getDetails(2).getPriceLabel());
    }

    @Test
    void unknownIdThrowsNotFound() {
        assertThrows(ConcertNotFoundException.class, () -> service.getDetails(99));
    }
}
