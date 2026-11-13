# Concert Tracker Backend

Java server for the Concert Tracker website (Sprint 3: business logic + persistence).

## Layers

| Layer | Code |
|---|---|
| UI | `concerttracker.com/index.html` (search bar script) |
| Application (business logic) | `controller/ConcertSearchController`, `service/ConcertSearchService`, `model/Concert` |
| Persistence | `persistence/ConcertRepository` (interface), `persistence/SqliteConcertRepository`, `concerts.db` |

`App.java` wires the layers together and starts the server.
`src/main/resources/schema.sql` creates the `concerts` table and sample data on every start.

## Run

Needs Java 17+ and Maven (IntelliJ can also open `backend/pom.xml` directly).

```
cd backend
mvn compile exec:java
```

Then open http://localhost:7070 and type in the search bar (try `drake`, `pop`, `country`, or `va`).

## Test

```
cd backend
mvn test
```

## API

`GET /api/concerts/search?q=<text>` (GitHub issue #2): returns **upcoming** concerts whose artist, genre, venue, or city contains the text (case-insensitive), soonest first.
A blank or missing `q` returns all upcoming concerts; no matches returns `[]` (the page shows "No concerts found").
Concerts with no posted price come back as "Price unavailable".

## Design docs

Diagrams for the search feature (Eilaf) are in `docs/diagrams/eilaf/`.
