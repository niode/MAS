


import java.lang.ProcessBuilder;
import java.io.*;


/**
 * Created by alexo on 2014-03-25.
 */
public class Tester {
	
	
    public static void main(String args[]) {

    	try{
    		routine(args);
    	}catch(Exception e){
    		e.printStackTrace();
    	}

    }
    
    
    private static void routine(String args[]) throws Exception{    	
    	
    	// assert proper input
        if(args.length != 2){
        	System.out.println("Incorrect usage of : >Java Tester <world list file> <rounds>");
        }
        // create local variables of input for convenience
        int rounds = Integer.parseInt(args[1]);
        String fileName = args[0];
        	
        // setup directory of worlds to try
        BufferedReader WORLDS = new BufferedReader( new FileReader(args[0]) ); 
        File theDir = new File(fileName+"_dir");
    	theDir.mkdir();
    	    
    	// iterate through world list for each world
        String world = null;
    	while ((world = WORLDS.readLine()) != null) {
    	
    	// PHASE 1 - run next simulation in list
    			
    		// make a new script for next world
    	    PrintWriter writer = new PrintWriter("testSuite.sh");
    	    writer.println("./run_java_agts_noviewer.sh "+world+".world "+rounds+" 7 MAR27MASTEST.jar Agent.Main "//replay.txt output.txt");
    	    +fileName+"_dir/"+world+"_replay.txt "
    	    +fileName+"_dir/"+world+"_output.txt"); 
    	    writer.close();
    	
    	    System.out.println("running: "+world); // go ahead and run the script
    	    ProcessBuilder pb = new ProcessBuilder("./testSuite.sh");
            Runtime rt = Runtime.getRuntime(); // give exec privilege to all shell scripts
            Process pr = rt.exec("chmod +x *.sh");
    	    Process p = pb.start();
    	    p.waitFor();

    	    System.out.println("world: "+world+" is complete.");
    	           
    	                
    	    
    	            
    	// PHASE 2 - parse results of simulation just run
    	    
    	    // connect the streams
    	    BufferedReader replayReader = new BufferedReader(new FileReader(fileName+"_dir/"+world+"_replay.txt"));
    	    BufferedReader outputReader = new BufferedReader(new FileReader(fileName+"_dir/"+world+"_output.txt"));
    	    PrintWriter summaryWriter   = new PrintWriter(fileName+"_dir/"+world+"_summary.txt");
    	 
    	    // pass them to summarizeResults method, ( this method is probably where you want to make any changes to the data you're getting from the sim)
    	    summarizeResults(replayReader,outputReader,summaryWriter);
    	}
    	
    	System.out.println("Tester is done");
	}

	

	private static void summarizeResults(BufferedReader replay, BufferedReader output, PrintWriter summary ) throws Exception{

        replay.close();
        output.close();
        summary.close();
		
		
		
	}

}
