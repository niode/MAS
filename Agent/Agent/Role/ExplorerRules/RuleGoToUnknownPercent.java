package Agent.Role.ExplorerRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;
import Ares.Commands.AgentCommands.SLEEP;

/**
 * If there exists cells with an unknown percent in range, move
 * towards it ensuring other explorers in range choose different
 * targets.
 * 
 * @author Daniel
 */
public class RuleGoToUnknownPercent implements Rule
	{
	/**
	 * The area around a claimed location to include in that claim.
	 * E.g. 1 = 3x3 area, 2 = 5x5 area, etc.
	 */
	private static final int RADIUS = 1;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		Location loc = sim.getAgentLocation(sim.getSelfID());
		int selfEnergy = sim.getAgentEnergy(sim.getSelfID());
		PathOptions opt = new PathOptions(loc);
		opt.shortest = false;
		Pathfinder pf = new Pathfinder(sim, opt);
		
		//Ensure there is a cell in range with an unknown percentage.
		//These could be in visited or unvisited cells.
		for (int i = 0; i < sim.getRowCount(); i++)
			for (int j = 0; j < sim.getColCount(); j++)
				{
				int percentage = sim.getPercentage(i, j);
				
				if (percentage > 0 && percentage < 100)
					{
					//Ensure no existing friendly agents there.
					boolean agentThere = false;
					for (AgentID id : sim.getAgentsAt(new Location(i, j)))
						if (id.getGID() == sim.getSelfID().getGID())
							{
							agentThere = true;
							break;
							}
					if (agentThere)
						continue;
					
					Path path = pf.getPath(new Location(i,j));
					
					//If there is an unknown in range, rule check true!
					if (path != null && path.getMoveCost() < selfEnergy)
						return true;
					}
				}
		
		return false;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		Location selfLoc = sim.getAgentLocation(sim.getSelfID());
		
		//Find all locations that have an unknown survivor chance.
		ArrayList<Location> unknownLocs = new ArrayList<Location>();
		for (int i = 0; i < sim.getRowCount(); i++)
			for (int j = 0; j < sim.getColCount(); j++)
				{
				int percentage = sim.getPercentage(i, j);
				if (percentage > 0 && percentage < 100)
					{
					Location unknownLoc = new Location(i,j);
					
					//Ignore locations a friendly agent is currently on, as we
					//will learn about it next turn.
					boolean agentThere = false;
					for (AgentID id : sim.getAgentsAt(unknownLoc))
						if (id.getGID() == sim.getSelfID().getGID())
							{
							agentThere = true;
							break;
							}
					if (agentThere)
						continue;
					
					//Save location.
					unknownLocs.add(unknownLoc);
					}
				}
		
		System.out.println(unknownLocs);
		
		//Find all explorer agents in range.
		ArrayList<AgentID> expInRange = new ArrayList<AgentID>();
		PathOptions opt = new PathOptions(selfLoc); 
		for (AgentID id : sim.getTeammates(Role.ID.EXPLORER))
			{
			opt.end = sim.getAgentLocation(id);
			int agentEnergy = sim.getAgentEnergy(sim.getSelfID());
			//if maxCost removes some agents, this may cause asymmetry
			Path path = Pathfinder.getPath(sim, opt);
			//If path exists, explorer is in range.
			if (path != null && path.getMoveCost() < agentEnergy)
				{
				expInRange.add(id);
				}
			}
		
		//Ensure explorers are in sorted order. I think they are, but just in case...
		Collections.sort(expInRange);
		
		//Create a preferred path list for all agents.
		ArrayList<ArrayList<Path>> allPaths = new ArrayList<ArrayList<Path>>();
		for (int i = 0; i < expInRange.size(); i++)
			allPaths.add(new ArrayList<Path>());
		
		//Calculate paths for all agents starting with lowest ID up to self.
		for (int i = 0; i < expInRange.size(); i++)
			{
			Location expLoc = sim.getAgentLocation(expInRange.get(i));
			int expEnergy = sim.getAgentEnergy(expInRange.get(i));
			ArrayList<Path> expPaths = allPaths.get(i);
			PathOptions otherOpt = new PathOptions(expLoc);
			otherOpt.shortest = false;
			
			//Calculate all paths for that agent.
			for (Location locInRange : unknownLocs)
				{
				//Get the location of each unknown cell in range.
				otherOpt.end = locInRange;
				
				//Calculate the path for the explorer.
				Path path = Pathfinder.getPath(sim, otherOpt);
				
				//Ignore empty paths that would kill the agent.
				if (path == null || path.getMoveCost() >= expEnergy)
					continue;
				
				expPaths.add(path);
				}
			
			//Don't bother calculating for higher ids than self.
			if (expInRange.get(i).equals(sim.getSelfID()))
				break;
			}
		
		//Create comparator to sort paths.
		Comparator<Path> pathSorter = new Comparator<Path>(){
			@Override
			public int compare(Path path1, Path path2)
				{
				return (int)(path1.getLength() - path2.getLength());
				}
		};
		
		//Sort the path lists of the other agents.
		for (int i = 0; i < expInRange.size(); i++)
			Collections.sort(allPaths.get(i), pathSorter);
		
		//If only one explorer or self is first.
		Location target = null;
		if (expInRange.size() == 1 || expInRange.get(0).equals(sim.getSelfID()))
			{
			//Just follow the first path.
			target = allPaths.get(0).get(0).getNext();
			}
		else
			{
			//Divvy up the targets starting with lowestID.
			ArrayList<Location> takenLocs = new ArrayList<Location>();
			
			//Flip through choices of each agent until finding one that
			//is not yet taken.
			for (int i = 0; i < expInRange.size(); i++)
				{
				//Look for target in the paths for explorer i
				for (Path nextPreferred : allPaths.get(i))
					{
					//Candidate target for explorer i
					Location candidateTarget = nextPreferred.getLast();
					
					//Ensure the target of this path is not already taken.
					for (Location taken : takenLocs)
						if (locCloseTo(candidateTarget, taken))
							{
							//End of path is taken, discard.
							candidateTarget = null;
							break;
							}
					
					if (candidateTarget != null)
						{
						//Found target to claim.
						//If this agent is self, can just go to target.
						if (sim.getSelfID().equals(expInRange.get(i)))
							{
							target = nextPreferred.getNext();
							}
						else
							{
							//Agent is not self, claim end of path target.
							takenLocs.add(candidateTarget);
							}
						
						//Found target for explorer i, stop looping through paths.
						break;
						}
					}
				
				//If target found for self, no need to check later agents.
				if (target != null)
					break;
				}
			}
		
		/*
		 * If no target, then all potentials were taken by agents with
		 * highest priority. Just take the favored path.
		 */
		if (target == null)
			{
			//Follow first path.
			ArrayList<Path> selfPaths = null;
			for (int i = 0; i < expInRange.size(); i++)
				if (expInRange.get(i).equals(sim.getSelfID()))
					{
					selfPaths = allPaths.get(i);
					break;
					}
			target = selfPaths.get(0).getNext();
			System.out.println("None available, going to first: "+selfPaths.get(0).getLast());
			}
		
		//If target is still null, sleep. Should not happen?
		if (target == null)
			return new SLEEP();
		
		//Move to target.
		return new MOVE(Pathfinder.getDirection(selfLoc, target));
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator,
	 * Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return null;
		}
	
	/**
	 * Checks if two locations are either the same, or besides each other.
	 * 
	 * @param a first path
	 * @param b second path
	 * @return true if the two paths are close together, else false.
	 */
	private boolean locCloseTo(Location a, Location b)
		{
		int rowDiff = Math.abs(a.getRow() - b.getRow());
		int colDiff = Math.abs(a.getCol() - b.getCol());
		
		if (rowDiff > RADIUS || colDiff > RADIUS)
			return false;
		
		return true;
		}

	}
