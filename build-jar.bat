@echo off
call .\gradlew.bat lwjgl3:jar
if not exist ".\Final Jar" mkdir ".\Final Jar"
copy /Y ".\lwjgl3\build\libs\Spel-1.0.0.jar" ".\Final Jar\donup.jar"
echo Klar! Jar-filen finns nu i ./Final Jar
pause
