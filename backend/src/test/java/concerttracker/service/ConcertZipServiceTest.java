package concerttracker.service;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import concerttracker.model.Concert;
import concerttracker.persistence.ConcertRepository;

/**
 * Tests the ZIP search rules in ConcertZipService.
 * Uses a fake repository (a list in memory), so no database or server is needed.
 */
class ConcertZipServiceTest {
    private static final LocalDate TODAY = LocalDate.now();

    // Fake data: id, name, genre, venue, city, zipCode, date, priceRange, seatRating, ticketUrl
    private final List<Concert> data = List.of(
            new Concert(1, "Fairfax Show", "Pop", "GMU", "Fairfax, VA", "22030",
                    TODAY.plusDays(20), "$20", "Great", "https://t.com"),
            new Concert(2, "Falls Church Show", "Jazz", "Hall", "Falls Church, VA", "22042",
                    TODAY.plusDays(5), null, "Great", "https://t.com"),
            new Concert(3, "Boston Show", "Rock", "Arena", "Boston, MA", "02134",
                    TODAY.plusDays(5), "$50", "Great", "https://t.com"),
            new Concert(4, "Old Show", "Pop", "GMU", "Fairfax, VA", "22030",
                    TODAY.minusDays(5), "$20", "Great", "https://t.com"),
            new Concert(5, "No ZIP Show", "Pop", "Somewhere", "Fairfax, VA", null,
                    TODAY.plusDays(5), "$20", "Great", "https://t.com"));

    private final ConcertRepository fakeRepo = () -> data;
    private final ConcertZipService service =
            new ConcertZipService(new ConcertSearchService(fakeRepo));

    @Test
    void findsConcertsInSameAreaSoonestFirst() {
        List<Concert> results = service.findNearZip("22030");
        assertEquals(2, results.size());
        assertEquals("Falls Church Show", results.get(0).getName());  // 5 days away
        assertEquals("Fairfax Show", results.get(1).getName());       // 20 days away
    }

    @Test
    void leavesOutPastConcerts() {
        for (Concert c : service.findNearZip("22030")) {
            assertFalse(c.getName().equals("Old Show"));
        }
    }

    @Test
    void skipsConcertsWithNoZip() {
        for (Concert c : service.findNearZip("22030")) {
            assertFalse(c.getName().equals("No ZIP Show"));
        }
    }

    @Test
    void keepsLeadingZero() {
        List<Concert> results = service.findNearZip("02134");
        assertEquals(1, results.size());
        assertEquals("Boston Show", results.get(0).getName());
    }

    @Test
    void returnsEmptyListWhenNothingNearby() {
        assertTrue(service.findNearZip("90210").isEmpty());
    }

    @Test
    void keepsPriceUnavailableLabel() {
        Concert fallsChurch = service.findNearZip("22042").get(0);
        assertEquals(ConcertSearchService.PRICE_UNAVAILABLE, fallsChurch.getPriceRange());
    }

    @Test
    void acceptsOnlyFiveDigitZips() {
        assertTrue(service.isValidZip("22030"));
        assertTrue(service.isValidZip(" 22030 "));
        assertFalse(service.isValidZip("2203"));
        assertFalse(service.isValidZip("220301"));
        assertFalse(service.isValidZip("abcde"));
        assertFalse(service.isValidZip(""));
        assertFalse(service.isValidZip(null));
    }
}