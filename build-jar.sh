#!/bin/bash
./gradlew lwjgl3:jar
cp ./lwjgl3/build/libs/Spel-1.0.0.jar ./donup.jar
echo "Klar! Jar-filen finns nu i projektmappen."
