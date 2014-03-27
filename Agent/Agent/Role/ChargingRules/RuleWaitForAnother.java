/**
 * 
 */
package Agent.Role.ChargingRules;

import java.util.LinkedList;
import java.util.List;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.ChargingRole;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.SLEEP;

/**
 * If done charging, but another agent is still charging
 * wait for them.
 * 
 * @author Daniel
 */
public class RuleWaitForAnother implements Rule
	{

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		//Get charging agents at own location.
		Location loc = sim.getAgentLocation(sim.getSelfID());
		List<AgentID> agents = sim.getAgentsAt(loc);
		LinkedList<AgentID> chargingAgents = new LinkedList<AgentID>();
		for (AgentID id : agents)
			{
			if (sim.getAgentRole(id) == Role.ID.CHARGER)
				chargingAgents.add(id);
			}
		
		//If only one, no others to wait for.
		if (chargingAgents.size() == 1)
			return false;
		
		//Check if agent is the only one charged.
		int chargedCount = 0;
		for (AgentID id : chargingAgents)
			{
			if (sim.getAgentEnergy(id) >= ChargingRole.getRequiredEnergy(sim))
				chargedCount++;
			}
		if (chargedCount > 1)
			return false;
		
		//There must be others still charging.
		return true;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//If waiting, may as well sleep for more energy.
		return new SLEEP();
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
