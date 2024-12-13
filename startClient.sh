#!/bin/bash

cd hagimule

echo
echo "--- BUILDING MAVEN PROJECT ---"
echo

mvn package

echo
echo "--- EXECUTING PACKAGE ---"
echo

cd target

jar=$(find -name "*.jar" | grep -E -o "hagimule.*" | sort -k 5 -n -r | head -n 1)

java -cp $jar fr.n7.hagimule.Client