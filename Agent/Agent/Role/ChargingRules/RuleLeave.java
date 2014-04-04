package Agent.Role.ChargingRules;

import Agent.Communicator;
import Agent.Simulation;
import Agent.State;
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
 * If no other agents will be ready soon, switch roles.
 */
public class RuleLeave implements Rule
{
	public boolean checkConditions(Simulation sim)
	{
		return true;
	}

	public AgentCommand doAction(Simulation sim, Communicator com)
	{
		//Going to switch roles, just sleep to get a little more energy.
		return new SLEEP();
	}

	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
	{
			return new ExplorerRole(sim, com, base);
	}

}
