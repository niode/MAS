package Agent.Role.Rules;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.Role;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;
import Ares.World.Objects.Rubble;
import Ares.World.Objects.WorldObject;

/**
 * If a nearby layer can be dug by one agent, go there.
 * 
 * @author Daniel
 */
public class RuleGoToNearSoloDig implements Rule
	{
	ArrayList<Location> digLocs = null;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		getNearDig(sim);
		
		return !digLocs.isEmpty();
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		if (digLocs == null || digLocs.isEmpty())
			getNearDig(sim);
		
		if (digLocs.isEmpty())
			return null;
		
		//Pick a random survivor location to move to.
		Random rand = new Random();
		Location target = digLocs.get(rand.nextInt(digLocs.size()));
		Location current = sim.getAgentLocation(sim.getSelfID());
		
		return new MOVE(Pathfinder.getDirection(current, target));
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return null;
		}
	
	/**
	 * Save locations of neighboring survivors.
	 * 
	 * @param sim
	 */
	private void getNearDig(Simulation sim)
		{
		digLocs = new ArrayList<Location>();
		
		Set<Location> neighbors = Pathfinder.getValidNeighbors(sim, sim.getAgentLocation(sim.getSelfID()));
		if (neighbors == null)
			return;
		
		for (Location near : neighbors)
			{
			//Get location top layer.
			WorldObject top = sim.getCell(near).getTopLayer();
			if (top instanceof Rubble && ((Rubble)top).getRemoveAgents() == 1 && sim.getPercentage(near) > 0)
				digLocs.add(near);
			}
		}
	}
