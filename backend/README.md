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

## Price sorting (Shuai Zheng)

`GET /api/concerts/sort?order=asc&q=<text>` sorts upcoming search results from low to high.
Use `order=desc` for high to low. If order is missing, ascending is used; invalid orders return 400.
Only GET is supported (other methods return 405).

The service compares the minimum price in a range, for example 80 for "$80-500".
Missing and invalid prices stay last in both directions. Equal prices keep their input order.
The sort endpoint reuses Eilaf's search rules, so it preserves the query and hides past concerts.

The website's Sort by menu calls the backend. Both cards and the Quick View table use the same
results. No sorting rules run in JavaScript. ZIP filtering and concert details are separate team features.

`ConcertPriceSortService` supports both repository-based sorting and list overloads used for
sorting search results without another database read. `ConcertPriceSortController` handles HTTP.
The existing Concert, repository classes, search service, schema and pom.xml were not changed.
The controller's JSON field names follow Eilaf's search endpoint.

Run `sh run-tests.sh` from backend for the original 22 checks with Java only.
Run `mvn test` for the original checks, existing search tests, and real HTTP + temporary SQLite tests.
The 22 original checks are grouped into one JUnit test; Maven test totals count test methods.

Updated diagram images are in the repository root (Shuai_Zheng_PriceSort_*.png).
The editable PPTX is in `docs/diagrams/shuai/`.

## Concert Display

ConcertDisplayService takes output of other services, and converts it into html format.
This format depends on the view mode, in which there are two options:
- Card: This mode is boxy and more visually appealing, with an image of the concert
- List: This mode is dense with information, allowing for easier lookup and comparison

The Quick View button on the website would perform a POST request.
