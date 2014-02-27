@ECHO OFF

IF "%1"=="" GOTO Skipit
IF "%2"=="" GOTO Skipit
IF "%3"=="" GOTO Skipit
IF "%4"=="" GOTO Skipit
IF "%5"=="" GOTO Skipit
IF "%6"=="" GOTO Skipit
IF "%7"=="" GOTO Skipit
IF NOT "%8"=="" GOTO Skipit

SET file=%1
SET rounds=%2
SET count=%3
SET cp=%4
SET main=%5
SET replay=%6
SET out=%7

ECHO "SCRIPT: worldfile: %file%" 
ECHO "SCRIPT: num rounds: %rounds%" 
ECHO "SCRIPT: count: %count%" 
ECHO "SCRIPT: classpath: %cp%" 
ECHO "SCRIPT: agent executable: %main%" 
ECHO "SCRIPT: output filename: %replay%" 
ECHO "SCRIPT: protocol filename: %out%"
 
ECHO "SCRIPT: Starting up ARES"

ECHO "java -jar Ares-System.jar -WorldFile %file% -NumRound %rounds% -ProcFile %replay% >> %out%"
start java -jar Ares-System.jar -WorldFile %file% -NumRound %rounds% -ProcFile %replay% >> %out%

TIMEOUT 3

ECHO "SCRIPT: Starting up the agents"

FOR /L %%x IN (1, 1, %count%) DO (
    ECHO "java -cp %cp% %main% localhost Test"
    start java -cp %cp% %main% localhost Test
)

TIMEOUT 1

ECHO "SCRIPT: Running Simulation"

ECHO "SCRIPT: Done."
PAUSE
GOTO End

:Skipit
ECHO "SCRIPT: USAGE: run_java_agts.bat <worldfile> <num rounds> <# agents> <classpath> <agent executable> <protocol filename> <output filename>"
ECHO "SCRIPT: EXAMPLE: run_java_agts.bat ExampleWorld.world 100 7 Ares-System.jar Agent.ExampleAgent.Main replay.txt output.txt"
PAUSE

:End