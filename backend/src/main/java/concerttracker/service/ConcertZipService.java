package concerttracker.service;

import java.util.ArrayList;
import java.util.List;

import concerttracker.model.Concert;

/** Business logic for finding upcoming concerts near a ZIP code. */
public class ConcertZipService {
    private final ConcertSearchService searchService;

    public ConcertZipService(ConcertSearchService searchService) {
        this.searchService = searchService;
    }

    /** Returns true if zip is exactly 5 digits. */
    public boolean isValidZip(String zip) {
        return zip != null && zip.trim().matches("\\d{5}");
    }

    /**
     * Returns upcoming concerts whose venue ZIP starts with the same 3 digits
     * (the same area), soonest first. Assumes isValidZip(zip) is true.
     */
    public List<Concert> findNearZip(String zip) {
        String area = zip.trim().substring(0, 3);
        List<Concert> results = new ArrayList<>();
        // Reuse search rules: upcoming only, soonest first, "Price unavailable" labels
        for (Concert c : searchService.search("")) {
            if (c.getZipCode() != null && c.getZipCode().startsWith(area)) {
                results.add(c);
            }
        }
        return results;
    }
}