if [ $# -eq 7 ];

then

file=$1;
rounds=$2;
count=$3;
cp=$4;
main=$5;
replay=$6;
out=$7;
echo "SCRIPT: worldfile: $file" ;
echo "SCRIPT: num rounds: $rounds" ;
echo "SCRIPT: classpath: $cp" ;
echo "SCRIPT: agent executable: $main" ;
echo "SCRIPT: output filename: $replay" ;
echo "SCRIPT: protocol filename: $out";

echo "SCRIPT: Starting up ARES"

echo "java -jar Ares-System.jar -WorldFile $file -NumRound $rounds -ProcFile $replay >> $out &"
java -jar Ares-System.jar -WorldFile $file -NumRound $rounds -ProcFile $replay >> $out &

sleep 3

echo "SCRIPT: Starting up the agents"

for ((  i = 0 ;  i < $count;  i++  ))
do
echo "java -cp $cp $main `hostname` Test&"
java -cp $cp $main `hostname` Test&
done

for ((  i = 0 ;  i < $count;  i++  ))
do
echo "java -cp $cp $main `hostname` TestTwo&"
java -cp $cp $main `hostname` TestTwo&
done

sleep 1

echo "SCRIPT: Running Simulation"

while [ `pgrep java` ];
do
       sleep 3
done

echo "SCRIPT: Done."

exit 128

fi

echo "SCRIPT: ARGS GIVEN: $#" ;
echo "SCRIPT: USAGE: ./run.sh <worldfile> <num rounds> <# agents> <classpath> <agent executable> <protocol filename> <output filename>"
echo "SCRIPT: EXAMPLE: ./run.sh ExampleWorld.world 100 7 Ares-System.jar Agent.ExampleAgent.Main replay.txt output.txt"

