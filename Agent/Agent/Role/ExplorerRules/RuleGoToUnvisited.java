package Agent.Role.ExplorerRules;

import java.util.Set;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;

/**
 * Move to the nearest cell that has never been visited yet.
 * 
 * @author Daniel
 */
public class RuleGoToUnvisited implements Rule
	{
	Path targetPath = null;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		findNearestUnvisited(sim);
		
		//None reachable if null.
		if(targetPath == null)
			return false;
		
		return true;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//If no saved path, do nothing.
		if (targetPath == null)
			return null;
		
		//Move to next location in path.
		Location nextLoc = targetPath.getNext();
		Location selfLoc = sim.getAgentLocation(sim.getSelfID());
		
		//Clear path.
		targetPath = null;
		return new MOVE(Pathfinder.getDirection(selfLoc, nextLoc));
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange()
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return null;
		}
	
	/**
	 * Finds the nearest unvisited reachable location.
	 * 
	 * @param sim simulation object
	 */
	private void findNearestUnvisited(Simulation sim)
		{
		targetPath = null;
		
		//Get unvisited tiles and current location.
		Set<Location> unvisited = sim.getUnvisited();
		Location selfLoc = sim.getAgentLocation(sim.getSelfID());
		
		//Check if unvisited tiles exist.
		if (unvisited.isEmpty())
			return;
		
		//Find nearest unvisited.
		long nearest = Long.MAX_VALUE;
		for (Location nextLoc : unvisited)
			{
			PathOptions opt = new PathOptions(selfLoc, nextLoc);
      opt.maxCost = sim.getAgentEnergy(sim.getSelfID());
			Path path = Pathfinder.getPath(sim, opt);
			if (path != null && path.getLength() < nearest)
				{
				nearest = path.getLength();
				targetPath = path;
				}
			}
		}
	}
