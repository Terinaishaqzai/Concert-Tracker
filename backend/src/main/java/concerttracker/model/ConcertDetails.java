package concerttracker.model;

/** What the details panel shows: the concert plus values worked out by the service. */
public class ConcertDetails {
    private final Concert concert;
    private final String status;
    private final long daysUntil;
    private final String priceLabel;

    /** Creates the details for one concert. */
    public ConcertDetails(Concert concert, String status, long daysUntil, String priceLabel) {
        this.concert = concert;
        this.status = status;
        this.daysUntil = daysUntil;
        this.priceLabel = priceLabel;
    }

    /** Returns the concert these details are for. */
    public Concert getConcert() {
        return concert;
    }

    /** Returns "Upcoming" or "Event Ended". */
    public String getStatus() {
        return status;
    }

    /** Returns how many days until the concert (0 if it is today or already over). */
    public long getDaysUntil() {
        return daysUntil;
    }

    /** Returns the price range, or "Price unavailable" if there is none. */
    public String getPriceLabel() {
        return priceLabel;
    }
}
