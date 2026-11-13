package concerttracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//import concerttracker.service.ConcertDisplayService;
import concerttracker.model.Concert;
import java.util.ArrayList;
import java.time.LocalDate;

public class ConcertDisplayServiceTest{

    public static void main(String[] args){
        String errText = "<p>Error, could not load data<p>\n";
        ConcertDisplayService service = new ConcertDisplayService();
        List<Concert> concerts = List().of(
            new Concert(0, "Steel Boots", "Rock", "Park Hall", "Fairfax", LocalDate.now(),
                   "$50-300", "Good", "https://www.seatgeek.com"),
            new Concert(0, "Fall Fest", "Country", "Fair Orchards", "Lorton", LocalDate.now(),
                   "$8", "Decent", "https://www.ticketmaster.com"),
            new Concert(0, "Boring Concert", "Ambience", "The Cavern", "CityNotFound", LocalDate.now(),
                   "$9999", "Dangerous", "https://google.com"));
        service.add(concerts);
        
        @Test
        void correctDefaultViewMode(){
            ConcertDisplayService serv = new ConcertDisplayService();
            assertEquals(serv.getViewMode(), "CARD");
        }
        
        @Test
        void correctToggleViewMode(){
            ConcertDisplayService serv = new ConcertDisplayService();
            serv.toggleViewMode();
            assertEquals(serv.getViewMode(), "LIST");
            serv.toggleViewMode();
            assertEquals(serv.getViewMode(), "CARD");
        }
        
        @Test
        void correctSetViewMode(){
            ConcertDisplayService serv = new ConcertDisplayService();
            serv.setViewMode("LIST");
            assertEquals(serv.getViewMode(), "LIST");
        }
        
        @Test
        void fixErrorSetViewMode(){
            ConcertDisplayService serv = new ConcertDisplayService();
            serv.setViewMode("FISH");
            assertEquals(serv.getViewMode(), "CARD");
        }
        
        @Test 
        void defaultGetHtml(){
            ConcertDisplayService serv = new ConcertDisplayService();
            assertEquals(service.getHTML(), "");
        }
        
        @Test
        void loadLayoutSucceeds(){
            ConcertDisplayService serv = new ConcertDisplayService();
            serv.toggleViewMode();
            assertTrue(!service.getHTML().equals(errText));
        }
        
        @Test
        void loadLayoutDiffers(){
            service.toggleViewMode();
            String html = service.getHTML();
            service.toggleViewMode();
            assertTrue(!service.getHTML().equals(html));
        }
    }
}