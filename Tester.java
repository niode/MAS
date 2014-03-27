


import java.lang.ProcessBuilder;
import java.io.*;
import java.util.Scanner;


/*
 	Created by alexo on 2014-03-25.
	

 */
public class Tester {

	private static boolean OVERWRITE = true;

	
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
    	
    	// PHASE 1 - run next world in list
    		
    		
    		if(OVERWRITE){
    		
	    		File file = new File(fileName+"_dir/"+world+"_replay.txt");
	    		if(file.delete()){
	    	//		System.out.println(file.getName() + " is deleted!");
	    		}else{
	    			System.out.println("Delete operation is failed.");
	    		}
	    		
	    		file = new File(fileName+"_dir/"+world+"_output.txt");
	    		if(file.delete()){
	    	//		System.out.println(file.getName() + " is deleted!");
	    		}else{
	    			System.out.println("Delete operation is failed.");
	    		}
	    		
	    		file = new File(fileName+"_dir/"+world+"_summary.txt");
	    		if(file.delete()){
	    //			System.out.println(file.getName() + " is deleted!");
	    		}else{
	    			System.out.println("Delete operation is failed.");
	    		}
 
    		}
    		// make a new script for next world
    	    PrintWriter writer = new PrintWriter("testSuite.sh");
    	   
	// !!! ### YOU WILL WANT TO CHANGE THE AGENT PATH IN THE LINE BELOW 'MAR27TEST.jar Agent.Main'  DEPENDING ON HOW YOU HAVE YOUR PROJECT SET UP
            writer.println("./run_java_agts_noviewer.sh worlds/"+world+".world "+rounds+" 7 MAR27MASTEST.jar Agent.Main "//replay.txt output.txt");
    	    +fileName+"_dir/"+world+"_replay.txt "
    	    +fileName+"_dir/"+world+"_output.txt"); 
    	    writer.close();
    	
            Runtime rt = Runtime.getRuntime(); // give exec privilege to all shell scripts ( may need to do this manualy in terminal also)
            Process pr = rt.exec("chmod +x *.sh");
    	    System.out.println("running: "+world); // go ahead and run the script
    	    ProcessBuilder pb = new ProcessBuilder("./testSuite.sh");
    	    Process p = pb.start();
    	    p.waitFor();
	  
    	    System.out.println("world: "+world+" is complete.");    	           
    	    p.destroy();              
    	    System.out.println("giving Ares "+(10+rounds)+" seconds to finish writing output and clears its ports.");
    	    Thread.sleep(10000+rounds*1000); // let Ares file writing finish and wait for ports to clear 
    	            
    	// PHASE 2 - parse results of simulation just run
    	    
    	    // connect the streams
    	    BufferedReader replayReader = new BufferedReader(new FileReader(fileName+"_dir/"+world+"_replay.txt"));
    	    BufferedReader outputReader = new BufferedReader(new FileReader(fileName+"_dir/"+world+"_output.txt"));
    	    PrintWriter summaryWriter   = new PrintWriter(fileName+"_dir/"+world+"_summary.txt");
    	 
    	    // parse simulation results in summarizeResults method
    	    summarizeResults(replayReader,outputReader,summaryWriter);
    	
    	
    	}
    	
    	
    	System.out.println("Tester is done");
	}

	

	private static void summarizeResults(BufferedReader replay, BufferedReader output, PrintWriter summary ) throws Exception{
		
		String line;
		
		int firstSave = -1;
		int lastSave = -1;
		
		int saved = -1;
		int alive = -1;
		int dead  = -1;
		
		int[] idle = new int[7]; 
		for( int i : idle) i = 0;
		
		int currentRound = 0;

		while ((line = output.readLine()) != null) {
		
		
			if(line.contains("Round:")){
				Scanner scanner = new Scanner(line);
				while(scanner.hasNext())
					try{
						currentRound = Integer.parseInt(scanner.next());
						break;
					}
					catch(Exception e){
						continue;
					}
			}
			
			
			if(line.contains("Group 1 saved") ){
				lastSave = currentRound;
				if(firstSave == -1 ){
					firstSave = currentRound;
				}
			}
			
			if(line.contains("GID 1 ] sent no action")){
				
				int agentNum = -1;
				Scanner scanner = new Scanner(line);
				while(scanner.hasNext()){
					try{
						agentNum = Integer.parseInt(scanner.next());
						break;
					}catch(Exception e){
						continue;
					}
				}
				
				if(agentNum > 0){
					idle[agentNum-1] += 1;
				}
				
			}
			
			
			if(line.contains("( GID 1 )")){
				


				Scanner scanner = new Scanner(line.substring(12));
				while(scanner.hasNext()){
					try{
						if( saved == -1 ){
						 saved = Integer.parseInt(scanner.next());
						 continue;
						}
						if( alive == -1){
						
							alive = Integer.parseInt(scanner.next());
							continue;
						}
						if( dead == -1){
							dead = Integer.parseInt(scanner.next());
							continue;
						}
						break;
					}catch(Exception e){
						continue;
					}
				}
				
			}

		
		
		}
		
			summary.println("Survivors saved : alive ( " + alive + " )   dead ( "+(saved-alive)+" )");
			summary.println("First save at round : "+firstSave);
			summary.println("Last save at round : "+lastSave);
			summary.println("Agent Idle Times :");
			for( int i = 0 ; i < idle.length ; i++){
				summary.println("Agent "+(i+1)+" idle : "+idle[i]+" rounds");
			}
			
	        replay.close();
	        output.close();
	        summary.close();
		
		
		
	}

}
