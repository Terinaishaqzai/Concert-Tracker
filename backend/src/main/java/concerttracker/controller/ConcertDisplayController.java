package concerttracker.controller;

import com.sun.net.httpserver.HttpExchange;
import concerttracker.model.Concert;
import concerttracker.service.ConcertDisplayService;
import concerttracker.service.ConcertSearchService;
import concerttracker.service.ConcertPriceSortService;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/* Receives search requests from the web page and returns results as JSON. */
public class ConcertDisplayController {
    private final ConcertDisplayService displayService;
    //private final ConcertSearchService searchService;
    //private final ConcertPriceSortService sortService;

    /* Creates the controller with the service which creates the html*/
    public ConcertDisplayController(ConcertDisplayService displayService) {
        this.displayService = displayService;
        //this.searchService = null;
        //this.sortService = null;
    }

    /* Handles POST /api/concerts/display["?"fish] HTTP/1.1 */
    public void handleButton(HttpExchange exchange) throws IOException {
        
        if (!exchange.getRequestMethod().equals("POST")) {
            return;
        }
        
        //String query = readQueryParam(exchange, "q");
    }
}