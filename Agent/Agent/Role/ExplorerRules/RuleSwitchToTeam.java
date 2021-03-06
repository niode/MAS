package Agent.Role.ExplorerRules;

import java.util.Set;
import Agent.Beacon;
import Agent.Communicator;
import Agent.Simulation;
import Agent.State;
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
 * If on or near a HELP_DIG beacon, switch to team if
 * there is another explorer in range that could help.
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
		digLocation = null;
		for (Beacon beacon : beacons)
			{
			Location beaconLoc = beacon.getLocation();

			int locrow = beaconLoc.getRow();
			int loccol = beaconLoc.getCol();
			
			if (locrow >= rowmin && locrow <= rowmax &&
					loccol >= colmin && loccol <= colmax &&
          !sim.isKiller(beaconLoc))
				{
				digLocation = beaconLoc;
				break; //Only need to know if at least 1 exists.
				}
			}
		
		//Check fails if no nearby dig location.
		if (digLocation == null)
			return false;
		
		//Ensure there are other Explorers in range that could help this one after the switch.
		for (AgentID id : sim.getTeammates(Role.ID.EXPLORER))
			{
			if (id.equals(sim.getSelfID()))
				continue; //Skip self.
			
			Location otherLoc = sim.getAgentLocation(id);
			if (otherLoc == null)
				continue; //Don't know why but sometimes others didn't have a location.
			
			PathOptions opt = new PathOptions(loc, otherLoc);
			opt.shortest = true;
			opt.maxCost = sim.getAgentEnergy(id);
			Path path = Pathfinder.getPath(sim, opt);
			
			if (path != null)
				return true;
			}

		//No explorers in range to team with.
		return false;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Change to team search state.
		sim.addAgentState(sim.getSelfID(), State.TEAM_SEARCH);

		// Test
		//System.out.printf("Agent %d: digLocation : %s\n", sim.getSelfID().getID(), digLocation);
		
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
