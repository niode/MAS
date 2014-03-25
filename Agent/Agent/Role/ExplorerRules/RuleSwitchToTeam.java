/**
 * 
 */
package Agent.Role.ExplorerRules;

import java.util.LinkedList;
import java.util.Set;
import Agent.Beacon;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.Role;
import Agent.Role.TeamRole;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Direction;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;

/**
 * If on or near a HELP_DIG beacon, and the nearest agent
 * is an explorer, switch to Team. The assumption here is
 * that the other explorer will switch to Team to help the
 * following turn.
 * 
 * @author Daniel
 */
public class RuleSwitchToTeam implements Rule
	{
	private Location digLocation = null;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		Location loc = sim.getAgentLocation(sim.getSelfID());
		int rowmin = loc.getRow() - 1;
		int rowmax = loc.getRow() + 1;
		int colmin = loc.getCol() - 1;
		int colmax = loc.getCol() + 1;
		
		Set<Beacon> beacons = sim.getBeaconType(Beacon.HELP_DIG);
		for (Beacon beacon : beacons)
			{
			Location beaconLoc = beacon.getLocation();
			int locrow = beaconLoc.getRow();
			int loccol = beaconLoc.getCol();
			
			if (locrow >= rowmin && locrow <= rowmax &&
					loccol >= colmin && loccol <= colmax)
				{
				digLocation = beaconLoc;
				break; //Only need to know if at least 1 exists.
				}
			}
		
		//Ensure the closest other agent is also Explorer.
		long closestDistance = Long.MAX_VALUE;
		AgentID closestAgent = null;
		for (AgentID id : sim.getTeammates())
			{
			Location otherLoc = sim.getAgentLocation(id);
			if (otherLoc == null)
				continue; //Other agent doesn't have a location?
			
			PathOptions opt = new PathOptions(loc, otherLoc);
			Path path = Pathfinder.getPath(sim, opt);
			
			if (path != null && path.getLength() < closestDistance)
				{
				closestDistance = path.getLength();
				closestAgent = id;
				}
			}
		
		if (closestAgent == null)
			return false;
		
		return sim.getAgentRole(closestAgent) == Role.ID.EXPLORER;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//If already at dig beacon, stay put.
		Location loc = sim.getAgentLocation(sim.getSelfID());
		if (digLocation.equals(loc))
			return new MOVE(Direction.STAY_PUT);
		
		//Otherwise, move towards dig beacon.
		return new MOVE(Pathfinder.getDirection(loc, digLocation));
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return new TeamRole(sim, com, base);
		}

	}
