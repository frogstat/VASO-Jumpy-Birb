@echo off
call .\gradlew.bat lwjgl3:jar
copy /Y ".\lwjgl3\build\libs\Spel-1.0.0.jar" ".\donup.jar"
echo Klar! Jar-filen finns nu i projektmappen.
pause
