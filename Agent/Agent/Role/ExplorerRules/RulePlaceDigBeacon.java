/**
 * 
 */
package Agent.Role.ExplorerRules;

import java.util.Set;
import Agent.Beacon;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.World.Cell;

/**
 * Place a HELP_DIG beacon on 100% survivor chance rubble piles
 * if no beacon yet exists.
 * 
 * @author Daniel
 */
public class RulePlaceDigBeacon implements Rule
	{

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		//Standing on 100% survivor chance cell?
		Location loc = sim.getAgentLocation(sim.getSelfID());
		Cell current = sim.getCell(loc);
		if (current.getPercentChance() != 100)
			return false;
		
		//Existing beacon already on cell?
		Set<Beacon> beacons = sim.getBeacons();
		for (Beacon beacon : beacons)
			{
			if (beacon.getLocation().equals(loc))
				return false;
			}
		
		return true;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Place HELP_DIG beacon.
		Location loc = sim.getAgentLocation(sim.getSelfID());
        com.send(new Beacon(Beacon.HELP_DIG, sim.getSelfID(), loc, Long.MAX_VALUE, 2));
		return null;
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
