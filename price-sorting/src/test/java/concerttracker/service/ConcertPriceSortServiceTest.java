package concerttracker.service;

import concerttracker.model.Concert;
import concerttracker.persistence.ConcertRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Standalone tests: run with run-tests.sh. No extra test libraries needed. */
public class ConcertPriceSortServiceTest {
    private static int passed = 0;

    public static void main(String[] args) {
        testAscending();
        testDescending();
        testMissingPrices();
        testInvalidPrices();
        testFormats();
        testEqualPrices();
        testOriginalList();
        testEmptyList();
        testSingleConcert();
        testAllMissing();
        testRepositoryCalls();
        testSearchResults();
        System.out.println("All " + passed + " tests passed.");
    }

    private static void testAscending() {
        ConcertPriceSortService service = service(
                concert(1, "$80-500"), concert(2, "$10-100"),
                concert(3, "$10-40"), concert(4, null),
                concert(5, "$120-450"), concert(6, "$800"), concert(7, "$95-300"));
        expectIds("ascending: supplied schema prices", service.sortByPriceAscending(), 2, 3, 1, 7, 5, 6, 4);
    }

    private static void testDescending() {
        ConcertPriceSortService service = service(
                concert(1, "$80-500"), concert(2, "$10-100"),
                concert(3, "$10-40"), concert(4, null),
                concert(5, "$120-450"), concert(6, "$800"), concert(7, "$95-300"));
        expectIds("descending uses minimum, not maximum", service.sortByPriceDescending(), 6, 5, 7, 1, 2, 3, 4);
    }

    private static void testMissingPrices() {
        ConcertPriceSortService service = service(concert(1, null), concert(2, ""),
                concert(3, "  "), concert(4, "Price unavailable"), concert(5, "$20"), concert(6, "$5"));
        expectIds("missing last ascending", service.sortByPriceAscending(), 6, 5, 1, 2, 3, 4);
        expectIds("missing last descending", service.sortByPriceDescending(), 5, 6, 1, 2, 3, 4);
    }

    private static void testInvalidPrices() {
        ConcertPriceSortService service = service(concert(1, "TBD"), concert(2, "NaN"),
                concert(3, "Infinity"), concert(4, "-$10"), concert(5, "$80-"),
                concert(6, "$80-40"), concert(7, "$1,2"), concert(8, "$1.999"),
                concert(9, "section 12"), concert(10, "$10-20-30"),
                concert(11, "9".repeat(400)), concert(12, "$80-bad"), concert(13, "$25"));
        int[] order = {13, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        expectIds("invalid last ascending", service.sortByPriceAscending(), order);
        expectIds("invalid last descending", service.sortByPriceDescending(), order);
    }

    private static void testFormats() {
        ConcertPriceSortService service = service(concert(1, "$1,200.50 - $2,000"),
                concert(2, "$80–500"), concert(3, " $ 45.50 — $90 "),
                concert(4, "0"), concert(5, "79.99"), concert(6, "$1,200.49"));
        expectIds("decimals, thousands, dashes and zero", service.sortByPriceAscending(), 4, 3, 5, 2, 6, 1);
        expectIds("formats descending", service.sortByPriceDescending(), 1, 6, 2, 5, 3, 4);
    }

    private static void testEqualPrices() {
        ConcertPriceSortService service = service(concert(1, "$10-100"), concert(2, "$10-40"), concert(3, "$10"));
        expectIds("ties ascending stay in original order", service.sortByPriceAscending(), 1, 2, 3);
        expectIds("ties descending stay in original order", service.sortByPriceDescending(), 1, 2, 3);
    }

    private static void testOriginalList() {
        List<Concert> original = new ArrayList<>(Arrays.asList(concert(1, "$80"), concert(2, "$10")));
        ConcertPriceSortService service = new ConcertPriceSortService(() -> original);
        List<Concert> sorted = service.sortByPriceAscending();
        expectIds("original list unchanged", original, 1, 2);
        if (sorted == original || sorted.get(0) != original.get(1)) {
            throw new AssertionError("Expected a new list holding the original Concert objects");
        }
        expectIds("copied list sorted", sorted, 2, 1);
    }

    private static void testEmptyList() {
        expectIds("empty ascending", service().sortByPriceAscending());
        expectIds("empty descending", service().sortByPriceDescending());
    }

    private static void testSingleConcert() {
        expectIds("single ascending", service(concert(1, "$80")).sortByPriceAscending(), 1);
        expectIds("single missing descending", service(concert(1, null)).sortByPriceDescending(), 1);
    }

    private static void testAllMissing() {
        ConcertPriceSortService service = service(concert(1, null), concert(2, "TBD"), concert(3, ""));
        expectIds("all missing ascending", service.sortByPriceAscending(), 1, 2, 3);
        expectIds("all missing descending", service.sortByPriceDescending(), 1, 2, 3);
    }

    private static void testRepositoryCalls() {
        final int[] calls = {0};
        ConcertRepository repository = new ConcertRepository() {
            @Override
            public List<Concert> findAll() {
                calls[0]++;
                return List.of(concert(calls[0], "$10"));
            }
        };
        ConcertPriceSortService service = new ConcertPriceSortService(repository);
        expectIds("ascending retrieves records", service.sortByPriceAscending(), 1);
        expectIds("descending retrieves fresh records", service.sortByPriceDescending(), 2);
        if (calls[0] != 2) {
            throw new AssertionError("Expected one findAll() call per sort");
        }
    }

    private static void testSearchResults() {
        // A fixed future date keeps this test independent of today's date.
        ConcertRepository repository = () -> List.of(concert(1, null), concert(2, "$80"), concert(3, "$10"));
        ConcertSearchService search = new ConcertSearchService(repository);
        ConcertPriceSortService service = new ConcertPriceSortService(() -> search.search("Test"));
        expectIds("compatible with search price label ascending", service.sortByPriceAscending(), 3, 2, 1);
        expectIds("compatible with search price label descending", service.sortByPriceDescending(), 2, 3, 1);
    }

    private static ConcertPriceSortService service(Concert... concerts) {
        // List.of is unmodifiable, so accidental in-place sorting would fail.
        return new ConcertPriceSortService(() -> List.of(concerts));
    }

    private static Concert concert(int id, String price) {
        return new Concert(id, "Test Concert " + id, "Pop", "Test Venue", "Test City",
                LocalDate.of(9999, 1, 1), price, "Great", "https://example.com");
    }

    private static void expectIds(String name, List<Concert> actual, int... expected) {
        if (actual.size() != expected.length) {
            throw new AssertionError(name + ": wrong result size");
        }
        for (int i = 0; i < expected.length; i++) {
            if (actual.get(i).getId() != expected[i]) {
                throw new AssertionError(name + ": wrong concert at position " + i);
            }
        }
        passed++;
        System.out.println("PASS: " + name);
    }
}
