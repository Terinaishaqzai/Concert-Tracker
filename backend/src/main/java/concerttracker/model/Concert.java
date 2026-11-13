package concerttracker.model;

import java.time.LocalDate;

/** A concert listing shown on the Quick View page. */
public class Concert {
    private final int id;
    private final String name;
    private final String genre;
    private final String venue;
    private final String city;
    private final String zipCode;
    private final LocalDate date;
    private final String priceRange;
    private final String seatRating;
    private final String ticketUrl;


    /** Creates a concert with all of its listing details; priceRange may be null. */
    public Concert(int id, String name, String genre, String venue, String city, String zipCode, LocalDate date,
                   String priceRange, String seatRating, String ticketUrl) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.venue = venue;
        this.city = city;
        this.zipCode = zipCode;
        this.date = date;
        this.priceRange = priceRange;
        this.seatRating = seatRating;
        this.ticketUrl = ticketUrl;
    }

    /** Returns the database id. */
    public int getId() {
        return id;
    }

    /** Returns the concert or artist name. */
    public String getName() {
        return name;
    }

    /** Returns the music genre, e.g. "Pop". */
    public String getGenre() {
        return genre;
    }

    /** Returns the venue name. */
    public String getVenue() {
        return venue;
    }

    /** Returns the city and state. */
    public String getCity() {
        return city;
    }
    
    /**
     * @author Greyson Buchanan
     * @return zipCode data of Concert.
     */
    public String getZipCode() {
        return zipCode;
    }

    /** Returns the concert date. */
    public LocalDate getDate() {
        return date;
    }

    /** Returns the ticket price range, e.g. "$80-500", or null if not posted. */
    public String getPriceRange() {
        return priceRange;
    }

    /** Returns the seat rating, e.g. "Great". */
    public String getSeatRating() {
        return seatRating;
    }

    /** Returns the ticket seller's page for this concert. */
    public String getTicketUrl() {
        return ticketUrl;
    }
}
