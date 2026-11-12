package concerttracker.service;

import concerttracker.model.Concert;
import concerttracker.persistence.ConcertRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.regex.Pattern;

/** Sorts concerts using the minimum ticket price in each price range. */
public class ConcertPriceSortService {
    private final ConcertRepository repository;

    public ConcertPriceSortService(ConcertRepository repository) {
        this.repository = repository;
    }

    public List<Concert> sortByPriceAscending() {
        return sortByPrice(true);
    }

    public List<Concert> sortByPriceDescending() {
        return sortByPrice(false);
    }

    private List<Concert> sortByPrice(boolean ascending) {
        // Copy the list so the repository's original order is not changed.
        List<Concert> concerts = new ArrayList<>(repository.findAll());

        concerts.sort(new Comparator<Concert>() {
            @Override
            public int compare(Concert first, Concert second) {
                OptionalDouble firstPrice = extractMinPrice(first.getPriceRange());
                OptionalDouble secondPrice = extractMinPrice(second.getPriceRange());

                // Check missing prices before checking the sort direction.
                if (firstPrice.isEmpty() && secondPrice.isEmpty()) {
                    return 0;
                }
                if (firstPrice.isEmpty()) {
                    return 1;
                }
                if (secondPrice.isEmpty()) {
                    return -1;
                }

                if (ascending) {
                    return Double.compare(firstPrice.getAsDouble(), secondPrice.getAsDouble());
                }
                return Double.compare(secondPrice.getAsDouble(), firstPrice.getAsDouble());
            }
        });

        // List.sort keeps the original order when prices are equal.
        return concerts;
    }

    /** Reads a single price or the first amount of a low-to-high range. */
    private OptionalDouble extractMinPrice(String priceRange) {
        if (priceRange == null || priceRange.isBlank()) {
            return OptionalDouble.empty();
        }

        String text = priceRange.trim().replace('\u2013', '-').replace('\u2014', '-');
        String[] prices = text.split("-", -1);
        if (prices.length > 2) {
            return OptionalDouble.empty();
        }

        // Accept dollars, decimal cents and correctly grouped thousands.
        String amountPattern = "\\$?\\s*(?:[0-9]+|[0-9]{1,3}(?:,[0-9]{3})+)(?:\\.[0-9]{1,2})?";
        for (String price : prices) {
            if (!Pattern.matches(amountPattern, price.trim())) {
                return OptionalDouble.empty();
            }
        }

        try {
            double minimum = Double.parseDouble(prices[0].replace("$", "").replace(",", "").trim());
            if (!Double.isFinite(minimum)) {
                return OptionalDouble.empty();
            }

            if (prices.length == 2) {
                double maximum = Double.parseDouble(prices[1].replace("$", "").replace(",", "").trim());
                if (!Double.isFinite(maximum) || maximum < minimum) {
                    return OptionalDouble.empty();
                }
            }
            return OptionalDouble.of(minimum);
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }
}
