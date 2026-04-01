#!/bin/bash
./gradlew lwjgl3:jar
mkdir -p "./Final Jar"
cp ./lwjgl3/build/libs/Spel-1.0.0.jar "./Final Jar/donup.jar"
echo "Klar! Jar-filen finns nu i ./Final Jar"
