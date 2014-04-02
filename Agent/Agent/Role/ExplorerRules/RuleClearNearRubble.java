/**
 * 
 */
package Agent.Role.ExplorerRules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.ChargingRole;
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
				if (ChargingRole.canStillCharge(sim,
						topRubble.getRemoveEnergy(), loc, sim.getSelfID()))
					{
					noMove = true;
					return true;
					}
				}
			}
		
		//Check neighboring tiles too.
		Set<Location> nearby = Pathfinder.getValidNeighbors(sim, loc, sim.getAgentEnergy(sim.getSelfID()));
		if (nearby == null)
			return false;
		for (Location possible : nearby)
			{
			WorldObject top = sim.getTopLayer(possible);
			if (top instanceof Rubble)
				{
				Rubble topRubble = (Rubble)top;
				if (topRubble.getRemoveAgents() < 2)
					{
					if (ChargingRole.canStillCharge(sim,
							topRubble.getRemoveEnergy(), loc, sim.getSelfID()))
						{
						target = possible;
						return true;
						}
					}
				}
			}
		
		//No neighboring rubble to clear. Move to the nearest one the agent can clear itself.
		//Start with all locations of rubble that takes 1 agent.
		ArrayList<Location> soloRubble = new ArrayList<Location>();
		for (int i = 0; i < sim.getRowCount(); i++)
			for (int j = 0; j < sim.getColCount(); j++)
				{
				Location rubbleLoc = new Location(i, j);
				int req = sim.getAgentsRequired(rubbleLoc);
				if (req < 2 && sim.getTopLayer(rubbleLoc) instanceof Rubble)
					soloRubble.add(rubbleLoc);
				}
		
		//If no places left to dig, false.
		if (soloRubble.isEmpty())
			return false;
		
		//Get all paths in range.
		PathOptions opt = new PathOptions(loc);
		opt.shortest = false;
		opt.maxCost = sim.getAgentEnergy(sim.getSelfID());
		List<Path> rubblePaths = Pathfinder.getPaths(sim, opt, soloRubble);
		ArrayList<Path> rubbleInRange = new ArrayList<Path>();
		for (Path path : rubblePaths)
			if (path != null)
				rubbleInRange.add(path);//TODO the null check may not be necessary.
		
		if (rubbleInRange.isEmpty())
			return false;
		
		Collections.sort(rubbleInRange, new Comparator<Path>(){
		@Override
		public int compare(Path path1, Path path2)
				{
				return (int)(path1.getLength() - path2.getLength());
				}
			});
		
		//Move to the nearest rubble pile.
		Path closest = rubbleInRange.get(0);
		if (closest.getLength() == 0)
			noMove = true;
		else
			target = closest.getNext();
		
		return true;
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
