package concerttracker.service;

import concerttracker.model.Concert;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;

/* Displays concerts */
public class ConcertDisplayService {

    private final String LISTLAYOUT = """
          <tr>
            <td>{NAME}</td>
            <td>{VENUE}</td>
            <td>{PRICE}</td>
            <td>{DATE}</td>
            <td>{SEATRATING}</td>
          </tr>""";
          
    private final String CARDLAYOUT = """
        <!--********** Element **********-->
        <li>
          <!-- Image -->
          <div class="image">
            <img src="https://placehold.co/90x90?text=Concert" alt="{NAME}">
          </div>
          <!-- Name + Location -->
          <div class="info">
            <p class="name">{NAME}</p>
            <p class="location">{VENUE}, {CITY}</p>
          </div>
          <!-- Price Range -->
          <div class="price">
            <p>{PRICE}</p>
          </div>
          <!-- Seat & Row + Seat Rating -->
          <div class="seat">
            <p class="rating">{SEATRATING}</p>
          </div>
          <!-- Date -->
          <div class="date">
            <p>{DATE}</p>
          </div>
          <!-- Buy: redirects to the vendor's ticket page -->
          <div class="buy">
            <a href="{LINK}" target="_blank">+ Buy</a>
          </div>
        </li>""";
    
    private List<Concert> results;
    private String concertHtml;
    
    /* List of Valid ViewModes
     * - CARD: Visually Appealing
     * - LIST: Information Dense
     */
    private List<String> viewModeList = List.of("CARD", "LIST");
    
    /* Determines which setting to do */
    private String concertViewMode;
    private final String defaultViewMode;
    
    /* Creates the service */
    public ConcertDisplayService() {
        concertHtml = "";
        results = new ArrayList<Concert>();
        defaultViewMode = "CARD";
        concertViewMode = defaultViewMode;
    }
    
    public String getHTML(){
        return concertHtml;
    }
    
    /* Converts a list of Concerts into JSON format.
     * This is called every time the results or view mode change.
     */
    private void generateHTML() {
        if (concertViewMode.compareTo("CARD") == 0) {
            concertHtml = generateCardHTML();
        }
        else if (concertViewMode.compareTo("LIST") == 0) {
            concertHtml = generateListHTML();
        }
        else {
            concertHtml = "";
        }
    }
    
    private String generateCardHTML() {
        String html = "";
        for (Concert concert : results) {
            String listElement = CARDLAYOUT+"";
            listElement = listElement.replaceAll("\\{NAME\\}", concert.getName())
            .replaceAll("\\{VENUE\\}", concert.getVenue())
            .replaceAll("\\{CITY\\}", concert.getCity())
            .replaceAll("\\{DATE\\}", concert.getDate().toString())
            .replaceAll("\\{PRICE\\}", Matcher.quoteReplacement(concert.getPriceRange()))
            .replaceAll("\\{SEATRATING\\}", concert.getSeatRating())
            .replaceAll("\\{LINK\\}", concert.getTicketUrl());
            html += listElement;
        }
        return html;
    }
    
    private String generateListHTML() {
        String html = "";
        for (Concert concert : results) {
            String listElement = LISTLAYOUT+"";
            listElement = listElement.replaceAll("\\{NAME\\}", concert.getName())
            .replaceAll("\\{VENUE\\}", concert.getVenue())
            .replaceAll("\\{CITY\\}", concert.getCity())
            .replaceAll("\\{DATE\\}", concert.getDate().toString())
            .replaceAll("\\{PRICE\\}", Matcher.quoteReplacement(concert.getPriceRange()))
            .replaceAll("\\{SEATRATING\\}", concert.getSeatRating())
            .replaceAll("\\{LINK\\}", concert.getTicketUrl());
            html += listElement;
        }
        return html;
    }
    
    /* Stores concert results.
     * This function takes output from 
     * Search, Sort, and Filter as input.
     */
    public void setConcertList(List<Concert> results) {
        this.results = results;
        generateHTML();
    }
    
    /* Toggles between the two existing view modes */
    public void toggleViewMode() {
        if (concertViewMode.compareTo("LIST") == 0) {
            setViewMode("CARD");
        }
        else {
            setViewMode("LIST");
        }
    }
    
    /* Gets the current concert view mode */
    public String getViewMode(){
        return concertViewMode;
    }
    
    /* Sets the current concert view mode */
    public void setViewMode(String newViewMode) {
        String oldViewMode = concertViewMode;
        /* If Invalid, Resets to Default */
        if (!viewModeList.contains(newViewMode)) {
            concertViewMode = defaultViewMode;
        }
        else {
            concertViewMode = newViewMode;
        }
        
        if (oldViewMode.compareTo(newViewMode) != 0){
            generateHTML();
        }
    }
}