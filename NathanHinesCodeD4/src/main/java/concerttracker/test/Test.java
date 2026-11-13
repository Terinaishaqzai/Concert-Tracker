package concerttracker.test;

import concerttracker.service.ConcertDisplayService;
import concerttracker.model.Concert;
import java.util.ArrayList;
import java.time.LocalDate;

public class Test{

    public static void main(String[] args){
        ConcertDisplayService serv = new ConcertDisplayService();
        ArrayList<Concert> concerts = new ArrayList();
        concerts.add(new Concert(0, "Steel Boots", "Rock", "Park Hall", "Fairfax", LocalDate.now(),
                   "$50-300", "Good", "https://google.com"));
        
        concerts.add(new Concert(0, "Fall Fest", "Country", "Fair Orchards", "Lorton", LocalDate.now(),
                   "$8", "Decent", "https://google.com"));
        
        // Intial Card View
        serv.setConcertList(concerts);
        System.out.println(serv.getHTML());
        
        // Toggle To List View (auto updates)
        serv.toggleViewMode();
        System.out.println(serv.getHTML());
        
        // Change concert list (auto updates)
        concerts.add(new Concert(0, "", "", "", "n", LocalDate.now(), "", "", ""));
        serv.setConcertList(concerts);
        System.out.println(serv.getHTML());

    }
}