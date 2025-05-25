#!/bin/sh
#
# Use this script to run your Spring Boot application LOCALLY.
#
# Note: Changing this script WILL NOT affect how CodeCrafters runs your program.
#
# Learn more: https://codecrafters.io/program-interface

set -e # Exit early if any commands fail

# Copied from .codecrafters/compile.sh
(
  cd "$(dirname "$0")" # Ensure compile steps are run within the repository directory

  mvn -B package -DskipTests -Ddir=/tmp/codecrafters-build-dir \
       -Dspring-boot.repackage.skip=false
)

# Run the Spring Boot application
# The spring-boot-maven-plugin creates an executable jar with a different name pattern
exec java -jar /tmp/codecrafters-build-dir/build-your-own-interpreter-1.0.jar "$@"
