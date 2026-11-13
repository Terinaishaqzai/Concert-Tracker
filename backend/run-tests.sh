#!/bin/sh
# Runs the original 22 checks with Java only, using the real backend classes.
# Run 'mvn test' as well for the HTTP + SQLite integration tests.
set -eu
backend=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
build=$(mktemp -d)
trap 'rm -rf "$build"' EXIT HUP INT TERM
src="$backend/src/main/java/concerttracker"
javac --release 17 -encoding UTF-8 -d "$build" \
    "$src/model/Concert.java" \
    "$src/persistence/ConcertRepository.java" \
    "$src/service/ConcertSearchService.java" \
    "$src/service/ConcertPriceSortService.java" \
    "$backend/src/test/java/concerttracker/service/ConcertPriceSortServiceTest.java"
java -cp "$build" concerttracker.service.ConcertPriceSortServiceTest
