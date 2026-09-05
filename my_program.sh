#!/bin/sh
mvn clean package -DskipTests
# Ensure the target jar name matches what is defined in your pom.xml
java -jar target/redis-java.jar "$@"