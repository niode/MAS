if [ $# -eq 2 ]; 

then

file=$1;
rounds=$2;
echo "SCRIPT: worldfile: $file" ;
echo "SCRIPT: num rounds: $rounds" ;

echo "SCRIPT: Starting up ARES"

echo "java -jar Ares-System.jar -WorldFile $file -NumRound $rounds &"
java -jar Ares-System.jar -WorldFile $file -NumRound $rounds &

exit 128

fi

echo "SCRIPT: ARGS GIVEN: $#" ;
echo "SCRIPT: USAGE: ./run.sh <worldfile> <num rounds>"
echo "SCRIPT: EXAMPLE: ./run.sh ExampleWorld.world 10"

