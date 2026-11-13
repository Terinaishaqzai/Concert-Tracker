package concerttracker.service;

import concerttracker.model.Concert;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.Scanner;

/* Displays concerts */
public class ConcertDisplayService {

    private List<Concert> results;
    private String concertHtml;
    
    /* List of Valid ViewModes
     * - CARD: Visually Appealing
     * - LIST: Information Dense
     */
    private ArrayList<String> viewModeList;
    
    /* Determines which setting to do */
    private String concertViewMode;
    private final String defaultViewMode;
    
    /* Creates the service */
    public ConcertDisplayService() {
        concertHtml = "";
        results = new ArrayList<Concert>();
        viewModeList = new ArrayList<String>();
        viewModeList.add("CARD");
        viewModeList.add("LIST");
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
        if (concertViewMode.equals("CARD")) {
            concertHtml = generateCardHTML();
        }
        else if (concertViewMode.equals("LIST")) {
            concertHtml = generateListHTML();
        }
        else {
            concertHtml = "";
        }
    }
    
    private String generateCardHTML() {
        Scanner sc;
        try {
            sc = new Scanner(new File("concerttracker/persistence/cardViewLayout.txt"));
        }
        catch(Exception e){
            return "<p>Error, could not load data<p>\n";
        }
        
        StringBuilder htmlBuilder = new StringBuilder();
        while(sc.hasNext()) {
                htmlBuilder.append(sc.nextLine());
                htmlBuilder.append("\n");
        }
        sc.close();
        
        String html = "";
        for (Concert concert : results) {
            String listElement = htmlBuilder.toString();
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
        Scanner sc;
        try {
            sc = new Scanner(new File("concerttracker/persistence/listViewLayout.txt"));
        }
        catch(Exception e){
            return "<p>Error, could not load data<p>\n";
        }
        
        StringBuilder htmlBuilder = new StringBuilder();
        while(sc.hasNext()) {
                htmlBuilder.append(sc.nextLine());
                htmlBuilder.append("\n");
        }
        sc.close();
        
        String html = "";
        for (Concert concert : results) {
            String listElement = htmlBuilder.toString();
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
        if (concertViewMode.equals("LIST")) {
            setViewMode("CARD");
        }
        else {
            setViewMode("LIST");
        }
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
        
        if (!oldViewMode.equals(newViewMode)){
            generateHTML();
        }
    }
}