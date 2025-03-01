#!/bin/sh
set -e # Exit early if any commands fail

# Build the project using Gradle
(
  cd "$(dirname "$0")"
  ./gradlew shadowJar
)

# Run the built JAR
exec java -jar build/libs/build-your-own-interpreter-all.jar "$@"
