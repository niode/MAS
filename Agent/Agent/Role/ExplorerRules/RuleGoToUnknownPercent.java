package Agent.Role.ExplorerRules;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;

/**
 * If near a cell with a percent that is between 0 and 100, explore it
 * 
 * @author Daniel
 */
public class RuleGoToUnknownPercent implements Rule
	{

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		Location loc = sim.getAgentLocation(sim.getSelfID());
		Set<Location> neighbors = Pathfinder.getValidNeighbors(sim, loc, sim.getAgentEnergy(sim.getSelfID()));
		
		//True if nearby cell with 0 < percent < 100.
		for (Location near : neighbors)
			{
			int percent = sim.getPercentage(near);
			if (percent > 0 && percent < 100)
				return true;
			}
		
		return false;
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Find all valid cells and move to a random one.
		Location loc = sim.getAgentLocation(sim.getSelfID());
		Set<Location> neighbors = Pathfinder.getValidNeighbors(sim, loc, sim.getAgentEnergy(sim.getSelfID()));
		
		ArrayList<Location> found = new ArrayList<Location>();
		for (Location near : neighbors)
			{
			int percent = sim.getPercentage(near);
			if (percent > 0 && percent < 100)
				found.add(near);
			}
		
		Random rand = new Random();
		Location target = found.get(rand.nextInt(found.size()));
		
		return new MOVE(Pathfinder.getDirection(loc, target));
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

	}
