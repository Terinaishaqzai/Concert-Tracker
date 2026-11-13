package concerttracker.service;

import org.junit.jupiter.api.Test;

/** Makes the existing 22 standalone checks run during mvn test as well. */
class ConcertPriceSortRegressionTest {
    @Test
    void runOriginalChecks() {
        ConcertPriceSortServiceTest.main(new String[0]);
    }
}
