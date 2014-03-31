/**
 * 
 */
package Agent.Role.ExplorerRules;

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
import Ares.Commands.AgentCommands.TEAM_DIG;
import Ares.World.Objects.Rubble;
import Ares.World.Objects.WorldObject;

/**
 * Clear any nearby rubble piles that only require 1 agent.
 * 
 * @author Daniel
 */
public class RuleClearNearRubble implements Rule
	{
	private boolean noMove = false;
	private Location target = null;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		noMove = false;
		target = null;
		
		Location loc = sim.getAgentLocation(sim.getSelfID());
		WorldObject topHere = sim.getTopLayer(loc);
		
		if (topHere instanceof Rubble)
			{
			Rubble topRubble = (Rubble)topHere;
			if (topRubble.getRemoveAgents() < 2)
				{
				noMove = true;
				return true;
				}
			}
		
		//Check neighboring tiles too.
		Set<Location> nearby = Pathfinder.getValidNeighbors(sim, loc);
		for (Location possible : nearby)
			{
			WorldObject top = sim.getTopLayer(possible);
			if (top instanceof Rubble)
				{
				Rubble topRubble = (Rubble)top;
				if (topRubble.getRemoveAgents() < 2)
					{
					target = possible;
					return true;
					}
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
		if (noMove)
			return new TEAM_DIG();
		
		Location loc = sim.getAgentLocation(sim.getSelfID());
		return new MOVE(Pathfinder.getDirection(loc, target));
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return null;
		}

	}
