-- Concert Tracker database: creates the concerts table and fills it with sample data.
-- Runs every time the server starts, so the sample data is always fresh.
-- price_range is NULL when the ticket seller has not posted a price yet.

DROP TABLE IF EXISTS concerts;

CREATE TABLE concerts (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT NOT NULL,
    genre       TEXT NOT NULL,
    venue       TEXT NOT NULL,
    city        TEXT NOT NULL,
    date        TEXT NOT NULL,
    price_range TEXT,
    seat_rating TEXT NOT NULL,
    ticket_url  TEXT NOT NULL
);

INSERT INTO concerts (name, genre, venue, city, date, price_range, seat_rating, ticket_url) VALUES
    ('Cool Concert',   'Pop',     'President''s Mall',    'Gopher Hills, VA', '2026-10-31', '$80-500',  'Great',   'https://www.ticketmaster.com'),
    ('Concert 1',      'Rock',    'MSG',                  'New York, NY',     '2026-11-26', '$10-100',  'Average', 'https://www.ticketmaster.com'),
    ('Boring Concert', 'Jazz',    'Center of the smARTs', 'Figly, VA',        '2026-11-26', '$10-40',   'Average', 'https://www.seatgeek.com'),
    ('Concert 2',      'Country', 'Staples Center',       'Los Angeles, CA',  '2026-11-27', NULL,       'Great',   'https://www.stubhub.com'),
    ('Drake Live',     'Hip-Hop', 'Capital One Arena',    'Washington, DC',   '2026-12-05', '$120-450', 'Great',   'https://www.ticketmaster.com'),
    ('Fancy Concert',  'Classical','High Life Orchards',  'Hopsfield, VA',    '2026-12-12', '$800',     'Great',   'https://www.stubhub.com'),
    ('Harry Styles',   'Pop',     'Jiffy Lube Live',      'Bristow, VA',      '2027-01-16', '$95-300',  'Average', 'https://www.ticketmaster.com');
