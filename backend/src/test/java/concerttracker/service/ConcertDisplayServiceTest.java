package concerttracker.service;

import concerttracker.model.Concert;
import java.util.List;
import java.time.LocalDate;

public class ConcertDisplayServiceTest{

   private static ConcertDisplayService service;
   public static void main(String[] args){
      service = new ConcertDisplayService();
      List<Concert> concerts = List.of(
            new Concert(0, "Steel Boots", "Rock", "Park Hall", "Fairfax", LocalDate.now(),
                   "$50-300", "Good", "https://www.seatgeek.com"),
            new Concert(0, "Fall Fest", "Country", "Fair Orchards", "Lorton", LocalDate.now(),
                   "$8", "Decent", "https://www.ticketmaster.com"),
            new Concert(0, "Boring Concert", "Ambience", "The Cavern", "CityNotFound", LocalDate.now(),
                   "$9999", "Dangerous", "https://google.com"));
      service.setConcertList(concerts);
      
      correctDefaultViewMode();
      correctToggleViewMode();
      correctSetViewMode();
      fixErrorSetViewMode();
      defaultGetHtml();
      loadLayoutDiffers();
   }
   
   private static void correctDefaultViewMode(){
      System.out.println("correctDefaultViewMode");
      ConcertDisplayService serv = new ConcertDisplayService();
      assertEquals(serv.getViewMode(), "CARD");
   }
        
   private static void correctToggleViewMode(){
      System.out.println("correctToggleViewMode");
      ConcertDisplayService serv = new ConcertDisplayService();
      serv.toggleViewMode();
      assertEquals(serv.getViewMode(), "LIST");
      serv.toggleViewMode();
      assertEquals(serv.getViewMode(), "CARD");
   }
        
   private static void correctSetViewMode(){
      System.out.println("correctSetViewMode");
      ConcertDisplayService serv = new ConcertDisplayService();
      serv.setViewMode("LIST");
      assertEquals(serv.getViewMode(), "LIST");
   }
        
   private static void fixErrorSetViewMode(){
      System.out.println("fixErrorSetViewMode");
      ConcertDisplayService serv = new ConcertDisplayService();
      serv.setViewMode("FISH");
      assertEquals(serv.getViewMode(), "CARD");
   }
        
   private static void defaultGetHtml(){
      System.out.println("defaultGetHtml");
      ConcertDisplayService serv = new ConcertDisplayService();
      assertEquals(serv.getHTML(), "");
   }
        

   private static void loadLayoutDiffers(){
      System.out.println("loadLayoutDiffers");
      service.toggleViewMode();
      String html = service.getHTML();
      service.toggleViewMode();
      assertTrue(service.getHTML().compareTo(html) != 0);
   }
   
   private static void assertEquals(String a, String b){
      if(a.compareTo(b) != 0){
         System.out.println("ERROR: " + a + " should equal " + b + "!");
      }
   }
   
   private static void assertTrue(boolean b){
      if(!b){
         System.out.println("ERROR: " + b + " should be true!");
      }
   }
    
}