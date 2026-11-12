#!/bin/sh
# Usage: sh run-tests.sh [path-to-team-backend]
set -eu
bundle=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
build=$(mktemp -d)
trap 'rm -rf "$build"' EXIT HUP INT TERM

# By default, compile against unchanged copies of Eilaf's supplied classes.
if [ "$#" -gt 0 ]; then
    shared="$1/src/main/java/concerttracker"
    model="$shared/model/Concert.java"
    repository="$shared/persistence/ConcertRepository.java"
    search="$shared/service/ConcertSearchService.java"
else
    model="$bundle/test-support/Concert.java"
    repository="$bundle/test-support/ConcertRepository.java"
    search="$bundle/test-support/ConcertSearchService.java"
fi

javac --release 17 -encoding UTF-8 -d "$build" \
    "$model" "$repository" "$search" \
    "$bundle/src/main/java/concerttracker/service/ConcertPriceSortService.java" \
    "$bundle/src/test/java/concerttracker/service/ConcertPriceSortServiceTest.java"
java -cp "$build" concerttracker.service.ConcertPriceSortServiceTest
