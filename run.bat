@ECHO OFF

IF "%1"=="" GOTO Skipit
IF "%2"=="" GOTO Skipit
IF NOT "%3"=="" GOTO Skipit

SET file=%1
SET rounds=%2
ECHO "SCRIPT: worldfile: %file%"
ECHO "SCRIPT: num rounds: %rounds%"
ECHO "SCRIPT: Starting up ARES"
ECHO "java -jar Ares-System.jar -WorldFile %file% -NumRound %rounds%"
start java -jar Ares-System.jar -WorldFile %file% -NumRound %rounds%
ECHO "SCRIPT: Done."
PAUSE
GOTO End

:Skipit
ECHO "SCRIPT: USAGE: run.bat <worldfile> <num rounds>"
ECHO "SCRIPT: EXAMPLE: run.bat ExampleWorld.world 10"
PAUSE

:End