package Agent.Role.ChargingRules;

import java.util.LinkedList;
import java.util.List;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.ChargingRole;
import Agent.Role.ExplorerRole;
import Agent.Role.Role;
import Agent.Role.TeamRole;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.SLEEP;

/**
 * If 2+ agents have finished charging, switch roles.
 * 
 * @author Daniel
 */
public class RuleStopCharging implements Rule
	{
	LinkedList<AgentID> chargedAgents = null;
	LinkedList<AgentID> chargingAgents = null;
	

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		//Verify 2+ agents done charging.
		Location loc = sim.getAgentLocation(sim.getSelfID());
		List<AgentID> agents = sim.getAgentsAt(loc);
		
		int chargeCount = 0;
		chargedAgents = new LinkedList<AgentID>();
		chargingAgents = new LinkedList<AgentID>();
		for (AgentID id : agents)
			{
			if (sim.getAgentRole(id) == Role.ID.CHARGER)
				{
				chargingAgents.add(id);
				if (sim.getAgentEnergy(id) >= ChargingRole.REQUIRED_ENERGY)
					{
					chargeCount++;
					chargedAgents.add(id);
					}
				}
			}
		
		return chargeCount >= 2;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Going to switch roles, just sleep to get a little more energy.
		return new SLEEP();
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		//If odd number of agents and agent is the highest id, become Explorer.
		if (chargedAgents.size() % 2 == 1)
			{
			int highest = 0;
			for (AgentID id : chargedAgents)
				if (id.getID() > highest)
					highest = id.getID();
			
			if (sim.getSelfID().getID() == highest)
				{
				//If other charging agents, wait for them.
				if (chargingAgents.size() > chargedAgents.size())
					return null;
				
				//No other agents to wait for, become Explorer.
				return new ExplorerRole(sim, com, base);
				}
			}
		
		//Otherwise become Team.
		return new TeamRole(sim, com, base);
		}

	}
